package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"io/ioutil"
	"os"
	"path/filepath"
	"sort"
	"strings"

	"testsuite"
)

type dirEnts []os.FileInfo

// Implement the sort interface for dirEnts
func (d dirEnts) Len() int {
	return len(d)
}

func (d dirEnts) Less(i, j int) bool {
	return d[i].Name() < d[j].Name()
}

func (d dirEnts) Swap(i, j int) {
	d[i], d[j] = d[j], d[i]
}

func main() {
	in := flag.String("in", "./vars.json", "")
	flag.Parse()

	// open file
	f, err := os.Open(*in)
	if err != nil {
		fmt.Printf("Error reading '%s': %s\n", *in, err)
		os.Exit(1)
	}

	// get file info
	fi, err := f.Stat()
	if err != nil {
		f.Close()
		fmt.Printf("Error reading '%s': %s\n", *in, err)
		os.Exit(1)
	}

	if !fi.IsDir() { // is a file
		err := forEachFile("./", *in)
		f.Close()
		if err != nil {
			fmt.Printf("Error combining '%s': %s\n", *in, err)
			os.Exit(1)
		}
	} else { // is a dir
		contents, err := f.Readdir(-1)
		f.Close()
		if err != nil {
			fmt.Printf("Error reading '%s': %s\n", *in, err)
			os.Exit(1)
		}

		// sort the contents, ensures lexical order
		sort.Sort(dirEnts(contents))

		for _, fi := range contents {
			// don't recursively read contents
			if fi.IsDir() {
				continue
			}

			// if it's not a json file, ignore it
			if !strings.HasSuffix(fi.Name(), ".json") {
				continue
			}

			err = forEachFile(*in, fi.Name())
			if err != nil {
				fmt.Printf("Error combining '%s': %s\n", fi.Name(), err)
				os.Exit(1)
			}
		}
	}
}

func forEachFile(subpath string, name string) error {
	in := filepath.Join(subpath, name)
	file, err := ioutil.ReadFile(in)
	if err != nil {
		return fmt.Errorf("Error reading '%s': %s", in, err)
	}

	var ttemp []testsuite.TestTemplate

	err = json.Unmarshal(file, &ttemp)
	if err != nil {
		fmt.Errorf("Error decoding '%s': %s", in, err)
	}

	vals, bits := testsuite.Flatten(ttemp)

	var tcase []byte = testsuite.GoCaseByCase(vals, bits)

	out := filepath.Join(subpath, strings.Join([]string{name, "vals"}, "."))
	err = ioutil.WriteFile(out, tcase, 0666)
	if err != nil {
		return fmt.Errorf("Error writing '%s': %s", out, err)
	}

	return nil
}
