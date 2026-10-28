const $ = id => document.getElementById(id);
const v = id => $(id).value.trim();
const n = id => v(id) === '' ? null : Number(v(id));
const checked = boxId => Array.from($(boxId).querySelectorAll('input:checked')).map(c => Number(c.value));
const base = () => $('base').value.trim();
const show = msg => { $('out').textContent = msg; };

$('base').value = location.protocol.startsWith('http') ? '' : 'http://localhost:8080';

async function getJson(path) {
  try {
    const res = await fetch(base() + path);
    if (!res.ok) return [];
    return await res.json();
  } catch (e) { return []; }
}

function fillSelect(id, list, labelFn, empty) {
  const sel = $(id);
  const prev = sel.value;
  sel.innerHTML = '';
  if (!list.length) {
    const o = document.createElement('option');
    o.value = '';
    o.textContent = empty || '(none available)';
    sel.appendChild(o);
    return;
  }
  for (const x of list) {
    const o = document.createElement('option');
    o.value = x.id !== undefined ? x.id : x.itemId;
    o.textContent = labelFn(x);
    sel.appendChild(o);
  }
  if ([...sel.options].some(o => o.value === prev)) sel.value = prev;
}

function fillChecks(id, list, labelFn) {
  const box = $(id);
  const keep = new Set(checked(id));
  box.innerHTML = '';
  if (!list.length) { box.textContent = '(none available)'; return; }
  for (const x of list) {
    const label = document.createElement('label');
    const cb = document.createElement('input');
    cb.type = 'checkbox';
    cb.value = x.id;
    cb.checked = keep.has(x.id);
    label.appendChild(cb);
    label.appendChild(document.createTextNode(' ' + labelFn(x) + '  '));
    box.appendChild(label);
  }
}

function setChecks(id, wantedIds) {
  const want = new Set(wantedIds);
  $(id).querySelectorAll('input').forEach(c => { c.checked = want.has(Number(c.value)); });
}

function addRow(tr, cells) {
  for (const c of cells) {
    const td = document.createElement('td');
    if (c instanceof Node) td.appendChild(c); else td.textContent = c;
    tr.appendChild(td);
  }
}

function makeTable(headers) {
  const t = document.createElement('table');
  t.border = 1;
  const hr = t.insertRow();
  for (const h of headers) { const th = document.createElement('th'); th.textContent = h; hr.appendChild(th); }
  return t;
}

function makeButton(text, fn) {
  const b = document.createElement('button');
  b.textContent = text;
  b.onclick = fn;
  return b;
}

const discountText = ds => (ds && ds.length) ? ds.map(d => d.percentage + '% off until ' + d.expireDate).join('; ') : '-';
const itemLabel = i => i.name + (i.brand ? ' (' + i.brand + ')' : '') + ' - ' + i.price;

async function call(method, path, body) {
  const opts = { method, headers: {} };
  if (body !== undefined) {
    opts.headers['Content-Type'] = 'application/json';
    opts.body = JSON.stringify(body);
  }
  show(method + ' ' + path + '\n...');
  try {
    const res = await fetch(base() + path, opts);
    const text = await res.text();
    let data = text, parsed = null;
    try { parsed = JSON.parse(text); data = JSON.stringify(parsed, null, 2); } catch (e) {}
    show(method + ' ' + path + '\n' + res.status + ' ' + res.statusText + '\n\n' + data);
    if (res.ok && method !== 'GET' && window.refreshAll) refreshAll();
    return res.ok ? parsed : null;
  } catch (e) {
    show('Request failed: ' + e.message);
    return null;
  }
}

const PAY_KEY = 'gms_payment_orders';
const trackedOrders = () => JSON.parse(localStorage.getItem(PAY_KEY) || '[]');
function trackOrder(orderId) {
  const l = trackedOrders();
  if (!l.includes(orderId)) { l.push(orderId); localStorage.setItem(PAY_KEY, JSON.stringify(l)); }
}

async function authPost(path, body) {
  try {
    const res = await fetch(base() + path, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    const text = await res.text();
    let data = null;
    try { data = JSON.parse(text); } catch (e) {}
    return { ok: res.ok, data: data, status: res.status };
  } catch (e) {
    return { ok: false, data: { message: 'Could not reach the server.' }, status: 0 };
  }
}

function errorMessage(r) {
  if (r.data && r.data.message) return r.data.message;
  if (r.data && typeof r.data === 'object') return Object.values(r.data).join(', ');
  return 'Something went wrong (status ' + r.status + ').';
}

async function send(method, path, body) {
  const opts = { method, headers: {} };
  if (body !== undefined) {
    opts.headers['Content-Type'] = 'application/json';
    opts.body = JSON.stringify(body);
  }
  try {
    const res = await fetch(base() + path, opts);
    const text = await res.text();
    let data = null;
    try { data = JSON.parse(text); } catch (e) {}
    return { ok: res.ok, data: data, status: res.status };
  } catch (e) {
    return { ok: false, data: { message: 'Could not reach the server.' }, status: 0 };
  }
}

function notify(text, isError) {
  const m = $('msg');
  if (!m) return;
  m.textContent = text;
  m.className = 'msg ' + (isError ? 'err' : 'ok');
  clearTimeout(notify.t);
  if (text) notify.t = setTimeout(() => { m.textContent = ''; m.className = 'msg'; }, 4000);
}
