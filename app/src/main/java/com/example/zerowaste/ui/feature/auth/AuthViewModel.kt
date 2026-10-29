package com.example.zerowaste.ui.feature.auth

import androidx.lifecycle.ViewModel
import com.example.zerowaste.data.repository.AuthRepository
import com.example.zerowaste.model.Utilizador
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel responsável pela gestão da autenticação e perfil do utilizador.
 * Vai atuar como meio que uma classe intermediária entre a interface e a
 * Firebase. Vai gerir o processo de Login, Registo, atualização de subscrições
 * e mantém os dados do utilizador atualizados por um SnapshotListener
 */
class AuthViewModel : ViewModel()
{
    private val repository = AuthRepository()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    //StateFlow para gerir mensagens de feedback apresentadas na interface
    private val _mensagem = MutableStateFlow<String?>(null)
    val mensagem: StateFlow<String?> = _mensagem.asStateFlow()

    //StateFlow que guarda o perfil do utilizador.
    private val _dadosUtilizador = MutableStateFlow<Utilizador?>(null)
    val dadosUtilizador = _dadosUtilizador.asStateFlow()

    /**
     * Inicialização do ViewModel
     * Assim que o mesmo é inicializado, verifica sempre se existe um utilizador
     * autenticado. Se assim existir, vai garantir que todas as alterações feitas
     * ao perfil do utilizador serão atualizadas automaticamente na interface, por
     * exemplo, subscrever ao premium.
     */
    init {
        val userID = auth.currentUser?.uid

        if (userID != null)
        {
            db.collection("utilizadores").document(userID)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists()) {
                        //Conversão do JSON para o modelo Utilizador
                        _dadosUtilizador.value = snapshot.toObject(Utilizador::class.java)
                    }
                }
        }
    }

    /**
     * Função que permite a um utilizador fazer login na aplicação.
     *
     * @param email Email introduzido pelo utilizador
     * @param password Password introduzida pelo utilizador
     */
    fun fazerLogin(email: String, password: String)
    {
        if(email.isBlank() || password.isBlank())
        {
            _mensagem.value = "You must fill in all the fields!"
            return
        }

        repository.login(email, password)
        {
            sucesso, erro -> if(sucesso)
            {
                _mensagem.value = "sucesso"
            }
            else
            {
                _mensagem.value = erro ?: "Unknown Error"
            }
        }
    }

    /**
     * Função que permite a um novo utilizador se registar na aplicação.
     *
     * @param primeiroNome Primeiro nome do utilizador
     * @param ultimoNome Último nome do utilizador
     * @param email Email para a conta do utilizador
     * @param numero Número de telemóvel do utilizador
     * @param password Palavra-passe escolhida pelo utilizador
     * @param familiaID Identificador de grupo para partilha de inventário (apenas se quiser)
     */
    fun fazerRegisto(primeiroNome: String, ultimoNome: String, email: String, numero: String, password: String, familiaID: String)
    {
        if(primeiroNome.isBlank() || ultimoNome.isBlank() || numero.isBlank() || email.isBlank() || password.isBlank())
        {
            _mensagem.value = "You must fill in all the fields!"
            return
        }

        repository.registar(primeiroNome, ultimoNome, email, numero, password, familiaID)
        {
                sucesso, erro -> if(sucesso)
            {
                _mensagem.value = "sucesso"
            }
            else
            {
                _mensagem.value = erro ?: "Unknown Error"
            }
        }
    }

    /**
     * Função que permite ao utilizador atualizar a sua subscrição de
     * Premium para Free ou vice-versa.
     *
     * @param novoEstado True se ativar o Premium, False para cancelar
     */
    fun upgradePremium(novoEstado: Boolean)
    {
        val userID = auth.currentUser?.uid

        if(userID != null)
        {
            db.collection("utilizadores").document(userID)
                .update("premium", novoEstado)
        }
    }

    /**
     * Função que permite atualizar o ID de uma nova família
     */
    fun trocaIDFamilia(novoID: String)
    {
        val userID = auth.currentUser?.uid

        if(userID != null)
        {
            db.collection("utilizadores").document(userID)
                .update("familiaID", novoID)
        }
    }

}
