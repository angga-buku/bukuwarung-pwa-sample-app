package tech.arifandi.bukuwarungpwasampleclient.shared.repositories

import tech.arifandi.bukuwarungpwasampleclient.feature.home.helper.HomeApi
import tech.arifandi.bukuwarungpwasampleclient.shared.k.KCredential

object ApiFactory {

    val homeApi = RetrofitFactory.retrofit(KCredential.Url)
        .create(HomeApi::class.java)
}