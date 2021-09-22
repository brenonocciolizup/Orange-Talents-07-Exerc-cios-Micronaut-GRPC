package br.com.zup.brenonoccioli

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.zup.brenonoccioli")
		.start()
}

