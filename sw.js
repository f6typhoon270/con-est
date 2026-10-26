// Con Est offline cache. Bump VERSION when the app changes so phones pick up the new copy.
const VERSION = "conest-v10";
const SHELL = ["./", "index.html", "pdf.min.js", "pdf.worker.min.js", "manifest.webmanifest", "icon-192.png", "icon-512.png"];
self.addEventListener("install", e=>{ e.waitUntil(caches.open(VERSION).then(c=>c.addAll(SHELL)).then(()=>self.skipWaiting())); });
self.addEventListener("activate", e=>{ e.waitUntil(caches.keys().then(ks=>Promise.all(ks.filter(k=>k!==VERSION).map(k=>caches.delete(k)))).then(()=>self.clients.claim())); });
self.addEventListener("fetch", e=>{
  const r = e.request; if(r.method!=="GET") return;
  const url = new URL(r.url);
  // the app itself: network first so updates arrive, cache when offline
  if(url.origin===location.origin){
    e.respondWith(fetch(r).then(res=>{ const cp = res.clone(); caches.open(VERSION).then(c=>c.put(r, cp)); return res; }).catch(()=>caches.match(r, {ignoreSearch:true}).then(m=>m || caches.match("index.html"))));
    return;
  }
  // fonts: cache first
  if(/fonts\.(googleapis|gstatic)\.com$/.test(url.hostname)){
    e.respondWith(caches.match(r).then(m=>m || fetch(r).then(res=>{ const cp = res.clone(); caches.open(VERSION).then(c=>c.put(r, cp)); return res; })));
  }
});
