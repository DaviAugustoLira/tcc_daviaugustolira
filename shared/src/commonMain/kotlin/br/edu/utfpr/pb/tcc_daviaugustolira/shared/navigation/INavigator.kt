package br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation

interface INavigator {
    fun navigate(screen: Screen)

    fun navigateBack()

    /**
     * Navega para [screen] removendo todo o back stack atual — usado após login
     * (não pode voltar ao Login) e após logout/expiração de sessão (não pode
     * voltar a uma rota admin).
     */
    fun navigateClearingBackStack(screen: Screen)
}
