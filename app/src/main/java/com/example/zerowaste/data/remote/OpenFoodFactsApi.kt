package com.example.zerowaste.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Configuração do modelo de dados para a integração de uma API
 * pública da Open Food Facts. Objetivo principal é permitir que
 * a aplicação consulte e preencha automaticamente os detalhes
 * do produto, neste caso, o nome, categoria e imagem a partir
 * da leitura do seu código de barras
 */

/**
 * Função que permite obter a resposta da API
 * da Open Food Facts
 *
 * @property status Propriedade que indica o estado do código de
 * barras. Se for 1, o código de barras existe, 0, caso contrário
 * @property product Propriedade que contém os detalhes do produto,
 * ou ?, se o produto não for encontrado
 */
data class OpenFoodFactsResponse(
    val status: Int,
    val product: OffProduto?
)

/**
 * Função que permite representar os detalhes de um produto
 * devolvido pela API da Open Food Facts. Aqui utiliza-se
 * [SerializedName] para se conseguir seguir o padrão de
 * linguagem do Kotlin de variáveis sem "_".
 *
 * @property productName Nome do produto extraído
 * @property categories Categoria do produto extraído
 * @property imageUrl URL da fotografia do produto extraído
 */
data class OffProduto(
    @SerializedName("product_name")
    val productName: String?,

    val categories: String?,

    @SerializedName("image_url")
    val imageUrl: String?
)

/**
 * Função que permite a interface definir os endpoints da
 * comunicação com a API do Open Food Facts
 */
interface OpenFoodFactsApi
{
    /**
     * Função que permite ler os dados de um produto através
     * do seu código de barras. Suspend para ser executada de
     * forma assíncrona, o que permite à aplicação não congelar
     *
     * @param barcode Código de barras a pesquisar na base de dados
     * @return [OpenFoodFactsResponse] Resposta que contém o estado
     * e os dados do produto
     */
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProduct(
        @Path("barcode") barcode: String
    ): OpenFoodFactsResponse
}

/**
 * Função que permite ter um Cliente Retrofit configurado para a API da
 * Open Food Facts.
 */
object RetrofitClient
{
    val api: OpenFoodFactsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodFactsApi::class.java)
    }
}
