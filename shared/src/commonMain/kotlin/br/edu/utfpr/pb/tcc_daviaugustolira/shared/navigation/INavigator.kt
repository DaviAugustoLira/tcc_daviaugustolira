package br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation

interface INavigator {
    fun navigate(screen: Screen)

    fun navigateBack()
}
