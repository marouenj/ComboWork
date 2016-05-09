package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"os"
	"path/filepath"
	"reflect"
	"testing"

	"github.com/marouenj/ComboWork/lang-agnostic/combine/combiner"
)

// convenience func for converting array of 'int' to array of 'interface'
func arrayOfInt_arrayOfInterface(old []int) []interface{} {
	new := make([]interface{}, len(old))
	for i, v := range old {
		new[i] = v
	}

	return new
}

// convenience func for converting array of 'string' to array of 'interface'
func arrayOfString_arrayOfInterface(old []string) []interface{} {
	new := make([]interface{}, len(old))
	for i, v := range old {
		new[i] = v
	}

	return new
}

func Test_unmarshal(t *testing.T) {
	testCases := []struct {
		json string
		v    combiner.Vars
	}{
		{ // test case 0, one var, one val
			`
			[
				{
					"vals": [1]
				}
			]
			`,
			combiner.Vars{
				combiner.Var{
					Vals: arrayOfInt_arrayOfInterface([]int{1}),
				},
			},
		},
		{ // test case 1, two vars
			`
			[
				{
					"vals": [1]
				},
				{
					"vals": ["one", "two"]
				}
			]
			`,
			combiner.Vars{
				combiner.Var{
					Vals: arrayOfInt_arrayOfInterface([]int{1}),
				},
				combiner.Var{
					Vals: arrayOfString_arrayOfInterface([]string{"one", "two"}),
				},
			},
		},
	}

	for idx, testCase := range testCases {
		// create temp dir
		dir, err := ioutil.TempDir("", "dir")
		if err != nil {
			t.Error(err)
		}

		// write json load to temp file
		filePath := filepath.Join(dir, "file")
		err = ioutil.WriteFile(filePath, []byte(testCase.json), 0666)
		if err != nil {
			t.Error(err)
		}

		file, err := ioutil.ReadFile(filePath)
		if err != nil {
			t.Error(err)
		}

		var v []combiner.Var

		err = json.Unmarshal(file, &v)
		if err != nil {
			t.Error(err)
		}

		// assert
		expected := fmt.Sprintf("%+v", testCase.v)
		actual := fmt.Sprintf("%+v", v)
		if !reflect.DeepEqual(expected, actual) {
			t.Errorf("[Test case %d], expected %+v, got %+v", idx, expected, actual)
		}

		// clean
		os.RemoveAll(dir)
	}
}
