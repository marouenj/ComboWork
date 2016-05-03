package bitutil

func Leftmost(val uint) uint {
	var num uint = 0
	for ; val != 0; val >>= 1 {
		num++
	}
	return num
}

func BitsPerVal(val uint) uint {
	if val == 0 {
		return 0
	}

	if val < 3 {
		return 1
	}

	num := Leftmost(val)

	if val&(val-1) == 0 {
		return num - 1
	}

	return num
}
