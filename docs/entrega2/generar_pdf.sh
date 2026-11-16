#!/bin/bash
# Genera el PDF de la Entrega 2 a partir de Entrega2_Diseno.html usando Google Chrome (Mac).
# En Windows: abrir Entrega2_Diseno.html en Chrome -> Imprimir -> Guardar como PDF
#             (Márgenes: Predeterminado, desactivar "Encabezados y pies de página").
cd "$(dirname "$0")"
CHROME="/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"

"$CHROME" --headless --disable-gpu --no-pdf-header-footer --virtual-time-budget=5000 \
  --print-to-pdf="PPAI2026_3K4_G1_E2_Diseño.pdf" \
  "file://$(pwd)/Entrega2_Diseno.html"

echo "PDF generado: $(pwd)/PPAI2026_3K4_G1_E2_Diseño.pdf"
