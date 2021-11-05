package com.ilsamil.conveniencemap.model

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name="servList")
data class ServList(
    @PropertyElement
    val estbDate: String,
    @PropertyElement
    val faclInfId: String,
    @PropertyElement
    val faclLat: Double,
    @PropertyElement
    val faclLng: Double,
    @PropertyElement
    val faclNm: String,
    @PropertyElement
    val faclRprnNm: String,
    @PropertyElement
    val faclTyCd: String,
    @PropertyElement
    val lcMnad: String,
    @PropertyElement
    val salStaDivCd: String,
    @PropertyElement
    val salStaNm: String,
    @PropertyElement
    val wfcltDivCd: String,
    @PropertyElement
    val wfcltId: String,
)