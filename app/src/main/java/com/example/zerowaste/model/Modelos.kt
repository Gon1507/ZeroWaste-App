package com.example.zerowaste.model

/**
 * Modelos de dados principais da aplicação. Representação das
 * entidades [Utilizador], que representa o perfil do mesmo e a
 * estrutura de um [Produto] no inventário e os respetivos estados
 * do mesmo.
 */

/**
 * Enumerado que define os estados possíveis do ciclo de vida de
 * um produto. Utilizado para se conseguir categorizar e filtrar
 * o inventário da aplicação.
 */
enum class EstadoProduto
{
    DISPONIVEL, CONSUMIDO, LIXO
}

/**
 * Classe que representa o perfil de um utilizador na aplicação.
 *
 * @property id Identificador único de um utilizador
 * @property primeiroNome Primeiro nome do utilizador
 * @property ultimoNome Último nome do utilizador
 * @property email Email do utilizador
 * @property numero Número de telemóvel do utilizador
 * @property premium Indica se o utilizador possui uma subscrição ativa ou não
 * @property familiaID Identificador para partilhar o inventário com outros membros
 */
data class Utilizador
(
    val id: String = "",
    val primeiroNome: String = "",
    val ultimoNome: String = "",
    val email: String = "",
    val numero: String = "",
    val premium: Boolean = false,
    val familiaID: String = ""
)

/**
 * Classe que representa um produto físico registado no inventário
 *
 * @property id Identificador único do produto
 * @property barcode Código de barras associado ao produto
 * @property nome Nome do produto
 * @property quantidade Quantidade atual de stock ao registar o produto
 * @property categoria Identificador da categoria a que o produto pertence
 * @property dataValidade Data limite de consumo do produto
 * @property local Identificador do local de armazenamento do produto
 * @property estado Estado atual do produto
 * @property userID Identificador do utilizador que registou o produto
 * @property familiaID Identificador do grupo familiar a quem o produto pertence
 * @property imagemURL URL da fotografia associado ao produto
 */
data class Produto
(
    val id: String = "",
    val barcode: String = "",
    val nome: String = "",
    val quantidade: String = "",
    val categoria: String = "",
    val dataValidade: String = "",
    val local: String = "",
    //Estado produto começa sempre em Disponível quando registado no inventário do utilizador
    val estado: String = EstadoProduto.DISPONIVEL.name,
    val userID: String = "",
    val familiaID: String = "",
    val imagemURL: String = ""
)

