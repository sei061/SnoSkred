package com.example.snoskred.model

data class Post (

    var RegId: Int,
    var RegionId: Int,
    var RegionName: String,
    var RegionTypeId: Int,
    var RegionTypeName: String,
    var DangerLevel: Int,
    var ValidFrom: String,
    var ValidTo: String,
    var NextWarningTime: String,
    var PublishTime: String,
    var MainText: String,
    var LangKey: Int
)