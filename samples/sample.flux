default files = FLUX_DIR;

files + "input.xml" |
open-file |
decode-xml |
handle-marcxml |
morph(files + "morph.xml") |
stream-tee | {
	encode-ntriples |
	write(files + "output.nt")
}{
	encode-dot |
	write(files + "output.dot")
};