"""Rebuild the hosted app (index.html) from src/con-est.src.html.

Run from the repo root:  python3 tools/build.py
Then bump VERSION in sw.js so installed phones pick up the update.
"""
import os
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
src = open(os.path.join(ROOT, "src/con-est.src.html")).read()
src = src.replace("__SAMPLE_PDF__", open(os.path.join(ROOT, "src/sample.b64")).read().strip())
cdn = "https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/"
src = src.replace(cdn + "pdf.min.js", "pdf.min.js").replace(cdn + "pdf.worker.min.js", "pdf.worker.min.js")
assert "cdnjs" not in src
index = open(os.path.join(ROOT, "index.html")).read()
head, rest = index.split("<body>\n", 1)
tail = rest[rest.rindex("\n<script>\nif(\"serviceWorker\""):]
open(os.path.join(ROOT, "index.html"), "w").write(head + "<body>\n" + src + tail)
print("index.html rebuilt")
