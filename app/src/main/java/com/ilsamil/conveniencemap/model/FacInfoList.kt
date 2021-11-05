package com.ilsamil.conveniencemap.model

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name="facInfoList")
data class FacInfoList(
    @PropertyElement
    val totalCount: Int,
    @Element
    val servList: ServList,
    @PropertyElement
    val resultCode: Int,
    @PropertyElement
    val resultMessage: String,
)