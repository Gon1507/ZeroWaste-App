package com.example.zerowaste.data.repository

import com.example.zerowaste.model.Utilizador
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Repositório responsável pela gestão da autenticação dos utilizadores.
 * Consegue então interagir com a Firebase Auth para se conseguir gerir
 * o Login / Registo e com o Firestore para armazenar e recuperar os
 * dados de perfil do utilizador.
 */

class AuthRepository
{
    //Uma instância da Firebase Auth para se conseguir gerir as sessões
    private val auth = FirebaseAuth.getInstance()

    //Uma instância da Firestore para guardar os dados do utilizador
    private val db = FirebaseFirestore.getInstance()

    /**
     * Função que realiza o Login de um utilizador existente utilizando o email
     * e password da Firebase. Utiliza um callback, a resposta, para não bloquear
     * a interface do utilizador.
     *
     * @param email Email do utilizador
     * @param pass Password do utilizador
     * @param resposta Função callback que retorna true se o login for bem-sucedido,
     * ou false em caso de falha
     */
    fun login(email: String, pass: String, resposta: (Boolean, String?) -> Unit)
    {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    resposta(true, null)
                }
                else
                {
                    resposta(false, task.exception?.message)
                }
            }
    }

    /**
     * Função que permite registar um novo utilizador na Firebase Auth e guarda o
     * perfil do mesmo na Firestore. A gestão de registo permite que os utilizadores
     * consigam partilhar inventários através do Modo Família
     *
     * @param primeiroNome Primeiro nome do utilizador
     * @param ultimoNome Primeiro nome do utilizador
     * @param email Email do utilizador
     * @param numero Número de telemóvel do utilizador
     * @param pass Password do utilizador
     * @param familiaID ID da família associada. Se for este campo for vazio, o ID
     * do próprio utilizador será definido como o ID da família
     * @param resposta Função callback com o resultado da operação de registo
     */
    fun registar(primeiroNome: String, ultimoNome: String, email: String, numero: String, pass: String, familiaID: String, resposta: (Boolean, String?) -> Unit)
    {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userID = auth.currentUser?.uid?: ""

                    //Defini a lógica do Modo de Família
                    //Se o campo do ID da família for vazio, o próprio utilizador
                    //cria a sua própria família
                    val familiaIDFinal =
                        if (familiaID.isNotBlank())
                        {
                            familiaID.trim()
                        }
                        else
                        {
                            userID
                        }

                    //Criação do novo utilizador
                    //Premium a false, pois contas novas criadas não têm acesso ao Premium
                    val utilizador = Utilizador(id = userID, primeiroNome = primeiroNome, ultimoNome = ultimoNome,
                        email = email, numero = numero, premium = false, familiaID = familiaIDFinal)

                    //Cria um objeto utilizadores e guarda o utilizador na Firestore
                    db.collection("utilizadores").document(userID).set(utilizador)
                        .addOnSuccessListener {
                            resposta(true, null)
                        }
                        .addOnFailureListener { e ->
                            resposta(false, "Account Created but errors: ${e.message}")
                        }
                }
                else
                {
                    resposta(false, task.exception?.message)
                }
            }
    }

    /**
     * Função que permite terminar a sessão do utilizador atual
     */
    fun logout()
    {
        auth.signOut()
    }
}
