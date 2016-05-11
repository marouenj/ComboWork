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

	"github.com/marouenj/ComboWork/lang-agnostic/combine/combiner"
)

const (
	combined string = "combined"
)

type dirEntries []os.FileInfo

// Implement the sort interface for dirEntries
func (d dirEntries) Len() int {
	return len(d)
}
func (d dirEntries) Less(i, j int) bool {
	return d[i].Name() < d[j].Name()
}
func (d dirEntries) Swap(i, j int) {
	d[i], d[j] = d[j], d[i]
}

func main() {
	// parse args
	baseDir := flag.String("base_dir", "./", "")
	flag.Parse()

	out := filepath.Join(*baseDir, combined)

	// check output dir exists or not
	info, err := os.Stat(out)
	if err != nil {
		if os.IsNotExist(err) {
			err := os.Mkdir(out, os.ModeDir|os.ModePerm)
			if err != nil {
				fmt.Printf("Error creating dir '%s': %s\n", out, err)
				os.Exit(1)
			}
		}
	} else {
		if !info.IsDir() {
			fmt.Printf("'%s' exists as a file (not dir)\n", out)
			os.Exit(1)
		}
	}

	// open file
	f, err := os.Open(*baseDir)
	if err != nil {
		fmt.Printf("Error reading '%s': %s\n", *baseDir, err)
		os.Exit(1)
	}

	// get file info
	fi, err := f.Stat()
	if err != nil {
		f.Close()
		fmt.Printf("Error reading '%s': %s\n", *baseDir, err)
		os.Exit(1)
	}

	if !fi.IsDir() { // is a file
		err := forEachFile("./", *baseDir)
		f.Close()
		if err != nil {
			fmt.Printf("Error combining '%s': %s\n", *baseDir, err)
			os.Exit(1)
		}
	} else { // is a dir
		contents, err := f.Readdir(-1)
		f.Close()
		if err != nil {
			fmt.Printf("Error reading '%s': %s\n", *baseDir, err)
			os.Exit(1)
		}

		// sort the contents, ensures lexical order
		sort.Sort(dirEntries(contents))

		for _, fi := range contents {
			// don't recursively read contents
			if fi.IsDir() {
				continue
			}

			// if it's not a json file, ignore it
			if !strings.HasSuffix(fi.Name(), ".json") {
				continue
			}

			err = forEachFile(*baseDir, fi.Name())
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

	var ttemp []combiner.Var

	err = json.Unmarshal(file, &ttemp)
	if err != nil {
		fmt.Errorf("Error decoding '%s': %s", in, err)
	}

	vals, bits := combiner.Flatten(ttemp)

	var tcase []byte = combiner.GoCaseByCase(vals, bits)

	out := filepath.Join(strings.Join([]string{subpath, combined, name}, "/"))
	err = ioutil.WriteFile(out, tcase, 0666)
	if err != nil {
		return fmt.Errorf("Error writing '%s': %s", out, err)
	}

	return nil
}
