package eu.alkismavridis.mathasmscript.main

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["eu.alkismavridis.mathasmscript"])
class MathasmscriptApplication

fun main(args: Array<String>) {
	runApplication<MathasmscriptApplication>(*args)
}
