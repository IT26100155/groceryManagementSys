// Navbar + login guard for the customer pages. Needs common.js and <div id="nav">.
const me = JSON.parse(localStorage.getItem('gms_customer') || 'null');
if (!me) location.replace('../index.html');
const cust = path => '/api/customers/' + me.id + path;

const ICON_CART = '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.7 13.4a2 2 0 0 0 2 1.6h9.7a2 2 0 0 0 2-1.6L23 6H6"/></svg>';
const ICON_USER = '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>';

function renderNav() {
  $('nav').innerHTML =
    '<div class="nav">' +
      '<a class="brand" href="customer.html">Grocery Store</a>' +
      '<div class="nav-right">' +
        '<a class="cart-link" href="cart.html" title="Cart">' + ICON_CART + '<span id="cart_count" class="badge">0</span></a>' +
        '<div class="account">' +
          '<button id="acct_btn" type="button">' + ICON_USER + '<span id="acct_name"></span> &#9662;</button>' +
          '<div id="acct_menu" class="menu" style="display:none">' +
            '<a href="account.html">My account</a>' +
            '<a href="#" id="logout_link">Log out</a>' +
          '</div>' +
        '</div>' +
      '</div>' +
    '</div>';
  $('acct_name').textContent = me.fullName || 'Account';
  $('acct_btn').onclick = e => {
    e.stopPropagation();
    const m = $('acct_menu');
    m.style.display = m.style.display === 'none' ? 'block' : 'none';
  };
  document.addEventListener('click', () => { $('acct_menu').style.display = 'none'; });
  $('logout_link').onclick = e => {
    e.preventDefault();
    localStorage.removeItem('gms_customer');
    location.replace('../index.html');
  };
}

async function refreshCartCount() {
  const cart = await getJson(cust('/cart'));
  const count = (cart.items || []).reduce((s, i) => s + i.quantity, 0);
  $('cart_count').textContent = count;
}

renderNav();
refreshCartCount();
