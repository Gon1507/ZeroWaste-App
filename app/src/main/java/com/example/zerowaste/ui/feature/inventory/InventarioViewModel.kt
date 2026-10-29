package com.example.zerowaste.ui.feature.inventory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerowaste.data.remote.RetrofitClient
import com.example.zerowaste.model.EstadoProduto
import com.example.zerowaste.model.Produto
import com.example.zerowaste.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel da aplicação.
 * Responsável por gerir todo o ciclo de vida dos produtos,
 * aplicar filtros de pesquisa locais, verificar códigos
 * de barras e integrar a Inteligência Artificial para dicas e receitas
 */

class InventarioViewModel : ViewModel()
{
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    //StateFlow para gerir mensagens de feedback apresentadas na interface
    private val _mensagem = MutableStateFlow<String?>(null)
    val mensagem = _mensagem.asStateFlow()

    //Variáveis para guardar o histórico de produtos para gerar estatísticas
    private val _estatisticas = MutableStateFlow<List<Produto>>(emptyList())
    val estatisticas = _estatisticas.asStateFlow()

    //Variável para a lista principal com todos os produtos
    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())
    val produtos = _produtos.asStateFlow()

    //Variáveis de estado para as interfaces relacionadas com AI
    var receitaGerada by mutableStateOf("")
        private set
    var aCarregarReceita by mutableStateOf(false)
        private set
    var dicaDia by mutableStateOf("")
        private set
    var aCarregarDica by mutableStateOf(false)
        private set

    //Variável de estado para verificar se o utilizador tem subscrição ou não
    var premium by mutableStateOf(false)
        private set

    //Variável para guardar o ID da família para partilha do inventário
    var familiaIDAtual by mutableStateOf("")
        private set

    //Variável para poder conectar o utilizador a uma família
    private var produtosListener: ListenerRegistration? = null

    //Variável com a lista mostrada ao utilizador após aplicados os filtros
    private val _produtosFiltros = MutableStateFlow<List<Produto>>(emptyList())
    val produtosSelect = _produtosFiltros.asStateFlow()

    //Variáveis de estado para a interface relacionadas a lista do inventário
    var localSelect by mutableStateOf("All")
        private set
    var ordenarPorValidade by mutableStateOf(false)
        private set

    //Companion object para se saber qual a data da última vez
    //que o utilizador pediu por uma receita e um contador para
    //limitar as contas gratuitas. Sem o mesmo, não era
    //guardado na memória
    companion object {
        private var dataUltimaReceita = ""
        private var numReceitasGeradasHoje = 0

        //O mesmo contexto para gerar uma dica diária
        private var dataUltimaDica = ""
        private var numDicasGeradasHoje = 0
    }

    /**
     * Inicialização do ViewModel
     * Assim que o mesmo é inicializado, verifica sempre se existe um utilizador
     * autenticado. Se assim existir, vai garantir que todas as alterações feitas
     * ao perfil do utilizador serão atualizadas automaticamente na interface, aplicar
     * filtros ao inventário.
     */
    init {
        val utilizadorAtual = auth.currentUser

        if(utilizadorAtual != null)
        {
            val userID = utilizadorAtual.uid

            //Produtos com filtros pelo ID do utilizador
            db.collection("produtos")
                .whereEqualTo("userID", userID)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        _produtos.value = snapshot.toObjects(Produto::class.java)
                        filtros()
                    }
                }

            //Produtos consumidos/lixo para estatísticas
            db.collection("produtos")
                .whereEqualTo("userID", userID)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        _estatisticas.value = snapshot.toObjects(Produto::class.java)
                    }
                }

            //Dados do perfil do utilizador Premium/Família
            db.collection("utilizadores").document(userID)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        if (snapshot.exists()) {
                            premium = snapshot.getBoolean("premium") ?: false

                            val novaFamiliaID = snapshot.getString("familiaID") ?: false

                            if(novaFamiliaID != familiaIDAtual)
                            {
                                familiaIDAtual = novaFamiliaID as String
                                produtosFamilia(familiaIDAtual)
                            }
                        }
                    }
                }
        }
    }

    /**
     * Função que permite gravar um produto no inventário do utilizador.
     *
     * @param nome Nome do produto
     * @param quantidade Quantidade de stock do produto
     * @param validade Data de limite de consumo do produto
     * @param local Local de armazenamento do produto
     * @param categoria Categoria de produto
     * @param imagemURL URL da fotografia do produto
     */
    fun gravarProduto(nome: String, quantidade: String, validade: String, local: String, categoria: String, imagemURL: String)
    {
        if(nome.isBlank() || local.isBlank() || quantidade.isBlank() || categoria.isBlank() || categoria.isBlank())
        {
            _mensagem.value = "You must fill all fields of the product!"
            return
        }

        val utilizadorAtual = auth.currentUser

        if (utilizadorAtual == null)
        {
            _mensagem.value = "Error: No user logged in!"
            return
        }

        //Referência para obter ID único
        val ref = db.collection("produtos").document()


        val produto =
            Produto(
                id = ref.id, nome = nome, dataValidade = validade, quantidade = quantidade, local = local,
                categoria = categoria, estado = EstadoProduto.DISPONIVEL.name, userID = utilizadorAtual.uid,
                familiaID = familiaIDAtual, imagemURL = imagemURL
            )

        //Mensagens de sucesso ou erro
        ref.set(produto)
            .addOnSuccessListener { _mensagem.value = "Added $nome to your inventory!"}
            .addOnFailureListener { error -> _mensagem.value = "Error: ${error.message}" }
    }

    /**
     * Função que permite ir procurar um produto à Open Food Facts.
     * Efetua uma chamada à API para procurar os dados de um produto
     * através do seu código de barras.
     *
     * @param barcode Código de barras
     * @onResultado Callback que retorna o nome, categoria e a imagem do produto
     */
    fun procurarProduto(barcode: String, onResultado: (nome: String, categoria: String, imagemURL: String) -> Unit)
    {
        viewModelScope.launch {
            try
            {
                val resposta = RetrofitClient.api.getProduct(barcode)

                if (resposta.status == 1 && resposta.product != null)
                {
                    val nomeProduto = resposta.product.productName ?: "Produto sem nome"
                    val categoriaProduto = resposta.product.categories?.substringBefore(",")?.trim() ?: ""
                    val imagemURLProduto = resposta.product.imageUrl ?: resposta.product.imageUrl ?: ""

                    onResultado(nomeProduto, categoriaProduto, imagemURLProduto)
                }
                else
                {
                    onResultado("Produto não encontrado", "", "")
                }
            } catch (_: Exception)
            {
                onResultado("Erro de ligação", "", "")
            }
        }
    }

    /**
     * Função que permite apagar um produto do inventário do utilizador e,
     * consequentemente da base de dados da Firestore
     *
     * @param idProduto ID do produto para ser apagado pelo utilizador
     */
    fun apagarProduto(idProduto: String)
    {
        db.collection("produtos").document(idProduto).delete()
    }


    /**
     * Função que permite ao utilizador, com a ajuda do AI, gerar uma
     * receita a partir dos seus ingredientes disponíveis no seu inventário
     */
    fun gerarReceita()
    {
        val hoje = LocalDate.now().toString()

        //Lógica para limitar contas gratuitas.
        //Utilizadores gratuitos só podem gerar uma receita por dia
        if(!premium)
        {
            if(dataUltimaReceita != hoje)
            {
                dataUltimaReceita = hoje
                numReceitasGeradasHoje = 0
            }

            if(numReceitasGeradasHoje >= 1)
            {
                receitaGerada = "Free plan limit reached (1 recipe per day). Upgrade to PRO for unlimited Michelin-star recipes!"
                return
            }
        }

        //Filtros para conseguir extrair apenas os produtos que o utilizador tem
        //no seu inventário e constam como disponíveis
        val listaProdutos = _produtos.value.filter { it.estado == EstadoProduto.DISPONIVEL.name }

        if(listaProdutos.isEmpty())
        {
            receitaGerada = "You don't have any ingredients to generate a recipe. Come back when you have some!"
            return
        }
        if(listaProdutos.size <= 2)
        {
            receitaGerada = "You have too few ingredients to generate a recipe. Come back when you have some more!"
            return
        }

        //Conversão de toda a lista de produtos do utilizador, separados por uma vírgula
        //para serem usados na prompt que será mandada ao agente
        val ingredientes = listaProdutos.joinToString(separator = ", ") {it.nome}

        //Prompt AI Agente para gerar uma receita
        val prompt = "I'm using a zero waste app. I have the following ingredients in my inventory: $ingredientes." +
            "Please suggest a simple and creative recipe that uses as many ingredients as possible to prevent them from going bad." +
            "Provide the steps in a concise format."

        aCarregarReceita = true
        receitaGerada = ""

        viewModelScope.launch {
            try
            {
                val aiModel = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = BuildConfig.AI_API_KEY
                )

                val respostaAI = aiModel.generateContent(prompt)

                receitaGerada = respostaAI.text ?: "Error: AI didn't get any text."

                //Aumenta a quota diária caso seja um utilizador gratuito
                if(!premium)
                {
                    numReceitasGeradasHoje++
                    dataUltimaReceita = hoje
                }
            }
            catch (e: Exception)
            {
                receitaGerada = "Oops! The AI Chef had a itty bitty tiny problem: ${e.message}"
            }
            finally
            {
                aCarregarReceita = false
            }
        }
    }

    /**
     * Função que permite ao utilizador, com a ajuda do AI, gerar uma
     * dica do dia, para tentar parar com o desperdício alimentar.
     * Mesma estrutura e contexto da função gerarReceita()
     */
    fun gerarDicaDia()
    {
        val hoje = LocalDate.now().toString()

        //Lógica para limitar contas gratuitas.
        //Utilizadores gratuitos só podem gerar uma receita por dia
        if(!premium)
        {
            if(dataUltimaDica != hoje)
            {
                dataUltimaDica = hoje
                numDicasGeradasHoje = 0
            }

            if(numDicasGeradasHoje >= 1)
            {
                dicaDia = "Free plan limit reached (1 daily tip per day). Upgrade to PRO for unlimited daily tips!"
                return
            }
        }

        val prompt = "Give me a short, actionable and creative daily tip about reducing food waste at home" +
            " or saving money on groceries. Provide only the tip, no intros or outros. Keep ir under 2 sentences"

        aCarregarDica = true
        dicaDia = ""

        viewModelScope.launch {
            try
            {
                val aiModel = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = BuildConfig.AI_API_KEY
                )

                val respostaAI = aiModel.generateContent(prompt)

                dicaDia = respostaAI.text ?: "Error"

                //Aumenta a quota diária caso seja um utilizador gratuito
                if(!premium)
                {
                    numDicasGeradasHoje++
                    dataUltimaDica = hoje
                }
            }
            catch (e: Exception)
            {
                dicaDia = "Erro Técnico: ${e.message}"
            }
            finally
            {
                aCarregarDica = false
            }
        }
    }

    /**
     * Função que permite atualizar o estado atual do produto, por exemplo,
     * mudar de DISPONIVEL para LIXO ou CONSUMIDO. É usada para estatísticas.
     *
     * @param idProduto ID do produto a trocar o estado
     * @param novoEstado O novo estado a ser atribuído
     */
    fun atualizarProdutoConsLixo(idProduto: String, novoEstado:String) {
        db.collection("produtos").document(idProduto)
            .update("estado", novoEstado)
    }

    /**
     * Função que permite implementar filtros no inventário do utilizador.
     * Neste caso, os filtros serão o de ordenar os produtos por data de validade
     * e por local de armazenamento
     */
    fun filtros()
    {
        var lista = _produtos.value

        lista = when (localSelect) {
            "All" -> lista
            else -> lista.filter { it.local == localSelect }
        }

        if(ordenarPorValidade)
        {
            lista = lista.sortedBy { produto ->
                val data = produto.dataValidade.split("/", "-")
                if(data.size == 3) "${data[2]}-${data[1]}-${data[0]}" else produto.dataValidade
            }
        }
        _produtosFiltros.value = lista
    }

    /**
     * Função que permite atualizar, então, o loca
     * de armazenamento do produto
     *
     * @param novoLocal O local a filtrar os produtos
     */
    fun atualizarLocal(novoLocal: String)
    {
        localSelect = novoLocal
        filtros()
    }

    /**
     * Função que permite atualizar, então, a ordem
     * pela qual os produtos são apresentados pela
     * sua data de validade
     */
    fun alternarOrdenacao()
    {
        ordenarPorValidade = !ordenarPorValidade
        filtros()
    }

    /**
     * Função que permite saber então quais os produtos
     * de uma determinada família
     */
    fun produtosFamilia(familiaID: String)
    {
        produtosListener?.remove()

        produtosListener = db.collection("produtos")
            .whereEqualTo("familiaID", familiaID)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val listaCompleta = snapshot.toObjects(Produto::class.java)

                    _produtos.value = listaCompleta
                    _estatisticas.value = listaCompleta
                    filtros()
                }
            }
    }
}
