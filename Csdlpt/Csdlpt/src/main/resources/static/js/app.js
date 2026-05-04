const API_BASE = '/api';

// --- Khởi tạo ---
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
});

// --- Auth ---
async function login() {
    const user = document.getElementById('username').value;
    const pass = document.getElementById('password').value;
    const err = document.getElementById('login-error');

    if (!user || !pass) {
        err.innerText = "Vui lòng nhập đủ thông tin!";
        return;
    }
    err.innerText = "Đang xử lý...";

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: user, password: pass })
        });
        
        if (!res.ok) {
            const msg = await res.text();
            throw new Error(msg || "Đăng nhập thất bại");
        }
        
        const data = await res.json();
        localStorage.setItem('jwt', data.token);
        localStorage.setItem('role', data.role);
        localStorage.setItem('username', data.username);
        localStorage.setItem('maNguon', data.maNguon);
        
        err.innerText = "";
        checkAuth();
        
    } catch (e) {
        err.innerText = e.message;
    }
}

function logout() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('role');
    localStorage.removeItem('username');
    checkAuth();
}

function checkAuth() {
    const token = localStorage.getItem('jwt');
    if (token) {
        document.getElementById('login-screen').classList.remove('active');
        document.getElementById('dashboard-screen').classList.add('active');
        
        // Setup UI
        document.getElementById('display-name').innerText = localStorage.getItem('username');
        const role = localStorage.getItem('role');
        document.getElementById('display-role').innerText = role;
        
        if (role === 'ROLE_ADMIN') {
            document.getElementById('menu-dlq').style.display = 'block';
            document.getElementById('node-badge').innerText = 'Node: Trụ Sở';
            document.getElementById('node-badge').style.background = '#e9d8fd';
            document.getElementById('node-badge').style.color = '#553c9a';
        } else {
            const maNguonStr = localStorage.getItem('maNguon') === 'MIEN_BAC' ? 'Miền Bắc' : 'Miền Nam';
            document.getElementById('menu-dlq').style.display = 'none';
            document.getElementById('node-badge').innerText = `Node: ${maNguonStr}`;
            document.getElementById('node-badge').style.background = '#c6f6d5';
            document.getElementById('node-badge').style.color = '#22543d';
        }

        // Tải dữ liệu mặc định
        switchTab('tab-tonkho');
    } else {
        document.getElementById('login-screen').classList.add('active');
        document.getElementById('dashboard-screen').classList.remove('active');
    }
}

// --- Fetch Helper (Tự động gắn JWT) ---
async function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem('jwt');
    const headers = { ...options.headers };
    if (token) headers['Authorization'] = `Bearer ${token}`;
    
    return fetch(url, { ...options, headers });
}

// --- Tabs & Modals ---
function switchTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.menu-item').forEach(el => el.classList.remove('active'));
    
    document.getElementById(tabId).classList.add('active');
    event.currentTarget.classList.add('active');

    // Load data
    if (tabId === 'tab-tonkho') loadTonKho();
    if (tabId === 'tab-lichsu') loadLichSu();
    if (tabId === 'tab-dlq') loadDlq();
    if (tabId === 'tab-yeucau') loadYeuCau();
}

function showModal(id) { document.getElementById(id).classList.add('active'); }
function hideModal(id) { document.getElementById(id).classList.remove('active'); }

// --- Chức Năng: Tồn Kho ---
async function loadTonKho(maVatTu = '') {
    const tbody = document.getElementById('table-tonkho');
    tbody.innerHTML = '<tr><td colspan="3" class="text-center">Đang tải...</td></tr>';
    
    const role = localStorage.getItem('role');
    let url = '';
    
    if (role === 'ROLE_ADMIN') {
        url = `${API_BASE}/tong-hop/ton-kho/mongo/MIEN_BAC`;
        if (maVatTu) url = `${API_BASE}/tong-hop/ton-kho/tim-kiem?maVatTu=${maVatTu}`;
    } else {
        url = `${API_BASE}/vattu/ton-kho`;
        // Local filtering not fully supported on backend yet, but this is a simple local node get
    }

    try {
        const res = await fetchWithAuth(url);
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(res.status + " " + errorText);
        }
        const data = await res.json();
        
        let html = '';
        // Handle both Array and Object (Mongo)
        let list = Array.isArray(data) ? data : data.chiTietMongo || [];
        
        // Simple client-side search if maVatTu is provided and role is not ADMIN
        if (maVatTu && role !== 'ROLE_ADMIN') {
            list = list.filter(item => item.maVatTu.includes(maVatTu));
        }
        
        if (list.length === 0) {
            html = '<tr><td colspan="3" class="text-center">Không có dữ liệu</td></tr>';
        } else {
            list.forEach(item => {
                html += `<tr>
                    <td>${item.maKho}</td>
                    <td>${item.maVatTu}</td>
                    <td><strong>${item.soLuongTon}</strong></td>
                </tr>`;
            });
        }
        tbody.innerHTML = html;
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="3" class="text-center status-failed">Lỗi: ${e.message}</td></tr>`;
    }
}

function searchTonKho() {
    const keyword = document.getElementById('search-vattu').value;
    loadTonKho(keyword);
}

// --- Nhập / Xuất ---
async function submitNhapKho() {
    const body = {
        maKho: document.getElementById('nhap-makho').value,
        maVatTu: document.getElementById('nhap-mavattu').value,
        tenVatTu: document.getElementById('nhap-tenvattu').value,
        soLuongTon: parseInt(document.getElementById('nhap-soluong').value)
    };
    
    try {
        const res = await fetchWithAuth(`${API_BASE}/vattu/nhap-kho`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(body)
        });
        const msg = await res.text();
        alert(msg);
        hideModal('modal-nhapkho');
        loadTonKho();
    } catch(e) { alert("Lỗi: " + e.message); }
}

async function submitXuatKho() {
    const body = {
        maKho: document.getElementById('xuat-makho').value,
        maVatTu: document.getElementById('xuat-mavattu').value,
        soLuongXuat: parseInt(document.getElementById('xuat-soluong').value)
    };
    try {
        const res = await fetchWithAuth(`${API_BASE}/vattu/xuat-kho`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(body)
        });
        const msg = await res.text();
        alert(msg);
        hideModal('modal-xuatkho');
        loadTonKho();
    } catch(e) { alert("Lỗi: " + e.message); }
}

// --- Lịch sử ---
async function loadLichSu() {
    const tbody = document.getElementById('table-lichsu');
    try {
        const res = await fetchWithAuth(`${API_BASE}/lich-su`);
        const data = await res.json();
        
        let html = '';
        data.forEach(item => {
            const statusClass = item.trangThai === 'THANH_CONG' ? 'status-success' : 'status-failed';
            html += `<tr>
                <td>${item.thoiGian.replace('T', ' ').substring(0, 19)}</td>
                <td><span class="badge" style="background:#edf2f7;color:#4a5568">${item.loaiGiaoDich}</span></td>
                <td>${item.maVatTu}</td>
                <td class="${statusClass}">${item.trangThai}</td>
                <td style="font-size:12px;color:#718096">${item.lyDoChiTiet || '-'}</td>
            </tr>`;
        });
        tbody.innerHTML = html || '<tr><td colspan="5" class="text-center">Trống</td></tr>';
    } catch(e) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center status-failed">Lỗi tải dữ liệu</td></tr>`;
    }
}

// --- DLQ ---
async function loadDlq() {
    const tbody = document.getElementById('table-dlq');
    try {
        const res = await fetchWithAuth(`${API_BASE}/admin/dlq`);
        const data = await res.json();
        let html = '';
        data.forEach(item => {
            const statusClass = item.trangThai === 'DANG_CHO' ? 'status-pending' : (item.trangThai==='DA_RETRY'?'status-success':'status-failed');
            html += `<tr>
                <td>${item.queueNguon}</td>
                <td style="font-size:12px">${item.lyDoLoi}</td>
                <td class="${statusClass}">${item.trangThai}</td>
                <td>${item.soLanThu}</td>
                <td>
                    <button class="btn-primary" style="padding:5px 10px;font-size:12px" onclick="retryDlq('${item.id}')">Retry</button>
                    <button class="btn-danger" style="padding:5px 10px;font-size:12px" onclick="deleteDlq('${item.id}')">Xóa</button>
                </td>
            </tr>`;
        });
        tbody.innerHTML = html || '<tr><td colspan="5" class="text-center">DLQ trống rỗng (Tuyệt vời!)</td></tr>';
    } catch(e) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center status-failed">Lỗi tải DLQ</td></tr>`;
    }
}

async function retryDlq(id) {
    await fetchWithAuth(`${API_BASE}/admin/dlq/${id}/retry`, { method: 'POST' });
    loadDlq();
}
async function deleteDlq(id) {
    await fetchWithAuth(`${API_BASE}/admin/dlq/${id}`, { method: 'DELETE' });
    loadDlq();
}
async function retryAllDlq() {
    const res = await fetchWithAuth(`${API_BASE}/admin/dlq/retry-tat-ca`, { method: 'POST' });
    const msg = await res.text();
    alert(msg);
    loadDlq();
}

// --- Yêu Cầu Điều Chuyển ---
async function loadYeuCau() {
    try {
        const res = await fetchWithAuth(`${API_BASE}/yeu-cau`);
        const data = await res.json();
        
        // Render Yêu cầu Đến
        const tbodyDen = document.getElementById('table-yeucau-den');
        let htmlDen = '';
        (data.nhanDuoc || data.tatCa || []).forEach(item => {
            const statusClass = item.trangThai === 'PENDING' ? 'status-pending' : (item.trangThai === 'APPROVED' ? 'status-success' : 'status-failed');
            let actions = '';
            if (item.trangThai === 'PENDING' && localStorage.getItem('role') !== 'ROLE_ADMIN') {
                actions = `
                    <button class="btn-primary" style="padding:5px 10px;font-size:12px" onclick="duyetYeuCau('${item.id}')">Duyệt</button>
                    <button class="btn-danger" style="padding:5px 10px;font-size:12px" onclick="tuChoiYeuCau('${item.id}')">Từ chối</button>
                `;
            }
            htmlDen += `<tr>
                <td>${item.ngayTao.replace('T', ' ').substring(0, 19)}</td>
                <td>${item.maNguonXin}</td>
                <td>${item.maVatTu}</td>
                <td>${item.soLuong}</td>
                <td class="${statusClass}">${item.trangThai}</td>
                <td>${actions}</td>
            </tr>`;
        });
        tbodyDen.innerHTML = htmlDen || '<tr><td colspan="6" class="text-center">Không có yêu cầu nào</td></tr>';

        // Render Yêu cầu Đi
        const tbodyDi = document.getElementById('table-yeucau-di');
        let htmlDi = '';
        (data.guiDi || []).forEach(item => {
            const statusClass = item.trangThai === 'PENDING' ? 'status-pending' : (item.trangThai === 'APPROVED' ? 'status-success' : 'status-failed');
            htmlDi += `<tr>
                <td>${item.ngayTao.replace('T', ' ').substring(0, 19)}</td>
                <td>${item.maNguonCho}</td>
                <td>${item.maVatTu}</td>
                <td>${item.soLuong}</td>
                <td class="${statusClass}">${item.trangThai}</td>
            </tr>`;
        });
        if (data.tatCa) {
            tbodyDi.innerHTML = '<tr><td colspan="5" class="text-center">Tài khoản ADMIN không hiển thị yêu cầu đi. Xem toàn bộ ở bảng trên.</td></tr>';
        } else {
            tbodyDi.innerHTML = htmlDi || '<tr><td colspan="5" class="text-center">Không có yêu cầu nào</td></tr>';
        }
    } catch(e) {
        console.error("Lỗi tải yêu cầu", e);
    }
}

async function submitYeuCau() {
    const body = {
        maKhoXin: document.getElementById('yc-makho-xin').value,
        maNguonCho: document.getElementById('yc-manguon-cho').value,
        maKhoCho: document.getElementById('yc-makho-cho').value,
        maVatTu: document.getElementById('yc-mavattu').value,
        soLuong: parseInt(document.getElementById('yc-soluong').value)
    };
    
    try {
        const res = await fetchWithAuth(`${API_BASE}/yeu-cau/tao`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(body)
        });
        const msg = await res.text();
        alert(msg);
        hideModal('modal-yeucau');
        loadYeuCau();
    } catch(e) { alert("Lỗi: " + e.message); }
}

async function duyetYeuCau(id) {
    if(!confirm("Bạn có chắc chắn duyệt xuất kho cho yêu cầu này?")) return;
    try {
        const res = await fetchWithAuth(`${API_BASE}/yeu-cau/${id}/duyet`, { method: 'POST' });
        if(!res.ok) throw new Error(await res.text());
        alert("Đã duyệt thành công!");
        loadYeuCau();
    } catch(e) { alert("Lỗi duyệt: " + e.message); }
}

async function tuChoiYeuCau(id) {
    try {
        const res = await fetchWithAuth(`${API_BASE}/yeu-cau/${id}/tu-choi`, { method: 'POST' });
        if(!res.ok) throw new Error(await res.text());
        alert("Đã từ chối!");
        loadYeuCau();
    } catch(e) { alert("Lỗi: " + e.message); }
}
