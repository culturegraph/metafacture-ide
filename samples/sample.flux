default files = ".";
default output = "file://" + files;

files + "input.xml" |
open("file") |
read("marcxml") |
morph(files + "morph.xml") |
stream-tee | {
	encode("n-triples") |
	write(output + "output.nt")
}{
	encode("dot") |
	write(output + "output.dot")
};