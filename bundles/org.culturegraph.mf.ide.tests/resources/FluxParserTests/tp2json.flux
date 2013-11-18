default gnd = FLUX_DIR + "Tp-200.pica.gz";

gnd|
open-file|
as-lines|
catch-object-exception |
decode-pica|
morph(FLUX_DIR + "tp2json.xml")|
encode-json|
write("stdout");
