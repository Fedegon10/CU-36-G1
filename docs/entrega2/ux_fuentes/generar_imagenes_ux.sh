#!/bin/bash
# Genera las imágenes de los afiches de UX (Mac) a partir de los HTML de esta carpeta.
# Resultado: ../imagenes/ux/moodboard.jpg, mapa_empatia.jpg, user_journey.jpg
cd "$(dirname "$0")"
CHROME="/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
DESTINO="../imagenes/ux"
mkdir -p "$DESTINO"

for afiche in moodboard mapa_empatia user_journey; do
  "$CHROME" --headless --disable-gpu --hide-scrollbars --force-device-scale-factor=2 \
    --window-size=1600,1100 --screenshot="$DESTINO/$afiche.png" "file://$(pwd)/$afiche.html" 2>/dev/null
  # Convierte a JPG (más liviano) y borra el PNG
  sips -s format jpeg -s formatOptions 88 "$DESTINO/$afiche.png" --out "$DESTINO/$afiche.jpg" >/dev/null
  rm "$DESTINO/$afiche.png"
  echo "Generado: $DESTINO/$afiche.jpg"
done
