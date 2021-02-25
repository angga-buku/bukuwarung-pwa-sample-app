package tech.arifandi.bukuwarungpwasampleclient.shared.listner

interface OnBackPressedListener {
    fun doBack()
    fun doBackWithStart()
}

interface OnItemClickListener {
    fun itemClicked(position: Int)
}