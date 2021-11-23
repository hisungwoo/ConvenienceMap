package com.ilsamil.conveniencemap.model

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name="facInfoList")
data class FacInfoList(
    @PropertyElement
    val totalCount: Int,
    @Element
    val servList: List<ServList>,
    @PropertyElement
    val resultCode: Int,
    @PropertyElement
    val resultMessage: String,
)

@Xml(name="servList")
data class ServList(
    @PropertyElement
    val estbDate: String?,   //설립일자
    @PropertyElement
    val faclInfId: String?,  // 순번
    @PropertyElement
    val faclLat: Double?,    // 시설위도
    @PropertyElement
    val faclLng: Double?,    // 시설경도
    @PropertyElement
    val faclNm: String?,     // 시설명
    @PropertyElement
    val faclRprnNm: String?, // 시설대표자성명
    @PropertyElement
    val faclTyCd: String?,   // 시설유형코드
    @PropertyElement
    val lcMnad: String?,     // 시설 기본주소
    @PropertyElement
    val salStaDivCd: String?,    // 영어상태구분코드 Y:영업, N:폐업
    @PropertyElement
    val salStaNm: String?,   //  영업상태구분명
    @PropertyElement
    val wfcltDivCd: String?, //  시설구분
    @PropertyElement
    val wfcltId: String?,    // 시설 ID
    
    
    @PropertyElement
    val evalInfo: String?,    // 기구표 목록
    @PropertyElement
    val srvInstId: String?,    // 시설 id



    

)