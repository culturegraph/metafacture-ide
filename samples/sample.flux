default files = FLUX_DIR;

files + "input.xml" |
open-file |
decode-xml |
handle-marcxml |
morph(files + "morph.xml") |
stream-tee | {
	encode-json |
	write(files + "output.jsonl")
}{
	encode-literals |
	write(files + "output.txt")
};
