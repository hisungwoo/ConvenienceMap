package com.ilsamil.conveniencemap.utils

class Util {
    fun changeType(type : String?) : String? {
        var result = type
        when (type) {
            "UC0A05" -> result = "우체국"
            "UC0J01" -> result = "체육관"
            "UC0H01" -> result = "어린이집"
            "UC0H03" -> result = "노인복지시설"
            "UC0I01" -> result = "생활권수련시설"
            "UC0A10" -> result = "한국장애인고용공단"
            "UC0G02" -> result = "유치원"
            "UC0A01" -> result = "수퍼마켓·일용품"
            "UC0A15" -> result = "지역아동센터"
            "UC0B01" -> result = "일반음식점"
            "UC0C02" -> result = "관람장"
            "UC0K03" -> result = "국민건강보험공단"
            "UC0G03" -> result = "초등학교"
            "UC0G08" -> result = "교육원·학원"
            "UC0F01" -> result = "종합병원"
            "UC0F03" -> result = "격리병원"
            "UC0L01" -> result = "일반숙박시설"
            "UC0N01" -> result = "주차장"
            "UC0O02" -> result = "전신전화국"
            "UC0B03" -> result = "안마시술소"
            "UC0G01" -> result = "특수학교"
            "UC0G09" -> result = "도서관"
            "UC0Q01" -> result = "화장시설"
            "UC0T02" -> result = "자연공원"
            "UC0V01" -> result = "기숙사"
            "UC0A02" -> result = "미용원·목욕장"
            "UC0O01" -> result = "방송국"
            "UC0C03" -> result = "집회장"
            "UC0H05" -> result = "사회복지시설"
            "UC0I02" -> result = "자연권수련시설"
            "UC0K02" -> result = "금융업소·일반업무시설"
            "UC0F02" -> result = "병원"
            "UC0L02" -> result = "관광숙박시설"
            "UC0N02" -> result = "운전학원"
            "UC0P01" -> result = "교도소·구치소"
            "UC0R02" -> result = "휴게소"
            "UC0T01" -> result = "도시공원"
            "UC0A03" -> result = "지역자치센터"
            "UC0A04" -> result = "파출소, 지구대"
            "UC0A07" -> result = "공공도서관"
            "UC0A14" -> result = "의원·한의원·산후조리원"
            "UC0B02" -> result = "휴게음식점·제과점"
            "UC0C01" -> result = "공연장"
            "UC0C04" -> result = "전시장"
            "UC0C05" -> result = "동·식물원"
            "UC0G04" -> result = "중학교"
            "UC0U04" -> result = "다세대주택"
            "UC0A12" -> result = "대피소"
            "UC0A08" -> result = "국민건강보험공단"
            "UC0A09" -> result = "국민연금공단"
            "UC0A11" -> result = "근로복지공단"
            "UC0M01" -> result = "공장"
            "UC0U01" -> result = "아파트"
            "UC0U03" -> result = "연립주택"
            "UC0A06" -> result = "보건소"
            "UC0A13" -> result = "공중화장실"
            "UC0K01" -> result = "국가·지자체 청사"
            "UC0K05" -> result = "한국장애인고용공단"
            "UC0G05" -> result = "고등학교"
            "UC0G06" -> result = "전문대학"
            "UC0G07" -> result = "대학교"
            "UC0S01" -> result = "장례식장"
            "UC0R01" -> result = "야외음악당·어린이회관"
            "UC0D01" -> result = "종교집회장"
            "UC0H02" -> result = "아동복지시설"
            "UC0H04" -> result = "장애인복지시설"
            "UC0J02" -> result = "운동장"
            "UC0K04" -> result = "국민연금공단"
            "UC0K06" -> result = "근로복지공단"
            "UC0E01" -> result = "도매·소매시장·상점"
            "UC0Q02" -> result = "봉안당"
            "UC0U02" -> result = "아파트 부대복리시설"
        }
        return result
    }

    fun chaneFaclType() {

    }


}






