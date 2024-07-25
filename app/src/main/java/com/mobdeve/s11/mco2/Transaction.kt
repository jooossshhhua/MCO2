package com.mobdeve.s11.mco2

data class Transaction(
    var amount : String ?= null,
    var category : String ?= null,
    var name : String ?= null,
    var date : String ?= null)
