package com.github.panpf.sketch.http

import java.util.LinkedList

class HttpHeaders(
    val addList: List<Pair<String, String>>,
    val setList: List<Pair<String, String>>,
) {

    fun isEmpty(): Boolean = addList.isEmpty() && setList.isEmpty()

    fun newBuilder(): Builder = Builder(this)

    override fun toString(): String {
        val addListString =
            addList.takeIf { it.isNotEmpty() }?.joinToString(prefix = "[", postfix = ",") {
                "${it.first},${it.second}"
            }
        val setListString =
            setList.takeIf { it.isNotEmpty() }?.joinToString(prefix = "[", postfix = ",") {
                "${it.first},${it.second}"
            }
        return if (addListString != null && setListString != null) {
            "HttpHeaders(adds=$addListString,sets=$setListString)"
        } else if (addListString != null) {
            "HttpHeaders(adds=$addListString)"
        } else if (setListString != null) {
            "HttpHeaders(sets=$setListString)"
        } else {
            "HttpHeaders()"
        }
    }


    class Builder {

        private val addList = LinkedList<Pair<String, String>>()
        private val setList = LinkedList<Pair<String, String>>()

        constructor()

        constructor(headers: HttpHeaders) {
            this.addList.addAll(headers.addList)
            this.setList.addAll(headers.setList)
        }

        fun add(name: String, value: String) {
            setList.removeAll {
                it.first == name
            }
            addList.add(name to value)
        }

        fun set(name: String, value: String) {
            removeAll(name)
            setList.add(name to value)
        }

        fun removeAll(name: String) {
            addList.removeAll {
                it.first == name
            }
            setList.removeAll {
                it.first == name
            }
        }

        fun setExist(name: String): Boolean = setList.find { it.first == name } != null

        fun addExist(name: String): Boolean = addList.find { it.first == name } != null

        fun build(): HttpHeaders = HttpHeaders(addList.toList(), setList.toList())
    }
}