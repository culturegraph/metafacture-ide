#!/bin/sh
scp -r org.culturegraph.mf.ide.p2/target/repository/* steeg@emphytos.hbz-nrw.de:/usr/local/lobid/src/lobid.org/download/tools/p2
scp -r org.culturegraph.mf.ide.p2/target/org.culturegraph.mf.ide.p2*.zip steeg@emphytos.hbz-nrw.de:/usr/local/lobid/src/lobid.org/download/tools/p2
scp org.culturegraph.mf.ide.p2/target/products/*.zip steeg@emphytos.hbz-nrw.de:/usr/local/lobid/src/lobid.org/download/tools
