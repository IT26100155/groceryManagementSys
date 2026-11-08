// Side-menu panel switcher for the customer account and cart pages.
// Menu items: <a data-panel="name" href="#name">. Panels: <section class="tab-panel" id="panel-name">.
function initPanels(defaultName) {
  const links = Array.from(document.querySelectorAll('.sidemenu [data-panel]'));
  const names = links.map(l => l.dataset.panel);

  function showPanel(name) {
    if (!names.includes(name)) name = defaultName;
    for (const l of links) {
      const on = l.dataset.panel === name;
      l.classList.toggle('active', on);
      if (on) l.setAttribute('aria-current', 'page'); else l.removeAttribute('aria-current');
    }
    for (const p of document.querySelectorAll('.tab-panel')) {
      p.hidden = p.id !== 'panel-' + name;
    }
  }
  window.showPanel = name => { history.replaceState(null, '', '#' + name); showPanel(name); };

  for (const l of links) {
    l.onclick = e => { e.preventDefault(); window.showPanel(l.dataset.panel); };
  }
  window.addEventListener('hashchange', () => showPanel(location.hash.slice(1)));
  showPanel(location.hash.slice(1));
}
