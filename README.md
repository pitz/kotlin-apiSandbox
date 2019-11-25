# Criando uma API Rest com Kotlin e SpringBot

Bom, atualmente minha default stack é o `Groovy` com o framework `Grails`. O Groovy é uma linguagem, que assim como o Kotlin, compila seu código para bytecode e é executada no mundo JVM. 

Vale ressaltar que esse projeto não tem como objetivo definir uma comparação entre Groovy e Kotlin **e sim, criar um ambiente favorável para aprender essa linguagem.**

Esse projeto tem como objetivo:

- Criar uma API Rest demonstrando a arquitetura MVC no Kotlin.
- Conectar um projeto Kotlin ao banco de dados.
- Utilizar injeção de dependências para instanciar Services em nosso Controller.
- Apresentar uma opção para utilizar autenticação no Kotlin.
- Apresentar uma solução para utilizar permissões por usuários.

Primeiro, é necessário criar uma projeto usando o Spring Initializr [https://start.spring.io/](https://start.spring.io/). Devido a minha origem com o Groovy, nesse projeto irei utilizar o Graddle. Crie o projeto da seguinte maneira:

- Defina a linguagem para **Kotlin**.
- Defina o **Group** e o **Artifact** como achar melhor. Eu sugiro você investir alguns minutos pesquisando sobre qual a melhor maneira para definir um pacote em Java. (Vide [https://bit.ly/2BLM8CZ](https://bit.ly/2BLM8CZ))
- Adicione as seguintes dependências: **Spring Web, Spring Data JPA e MySQL Driver.**

Pronto, basta confirmar e baixar o .zip do projeto criado.

Agora que o projeto está criado, podemos começar a de fato *codar*. Vou utilizar o IntelliJ IDEA Ultimate como IDE. Ao abrir o projeto a primeira vez, o build pode demorar um pouco. A primeira etapa está concluída: Temos um projeto fresquinho esperando receber código. Com isso feito, vamos configurar a conexão com o banco de dados.

### Configurando o MySQL

Tratarei a configuração do Spring como algo totalmente simples, e é. Vamos configurar o `application.properties` para permitir que você crie suas tabelas automaticamente, utilizando a abordagem Code First. Para isso, você pode seguir o seguinte passo-a-passo:

- Primeiro crie seu schema no MySql.
- Agora, configure o `application.properties` da seguinte maneira:

    spring.datasource.url = jdbc:mysql://localhost/<insira aqui o nome do seu schema>?useSSL=false&allowPublicKeyRetrieval=true
    spring.datasource.username = <username do seu banco>
    spring.datasource.password = <senha do seu banco>
    
    ## Hibernate Properties
    spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL5InnoDBDialect
    spring.jpa.hibernate.ddl-auto = update

Lembra-se de alterar a url, senha e usuário conforme citado no código acima. 

Um ponto relevante sobre a configuração, fica por conta do `spring.jpa.hibernate.ddl-auto`, essa configuração irá definir o comportamento do spring <-> seu banco de dados quando uma alteração ocorrer. No nosso caso, vamos manter o valor como `update`. Desse modo, ao atualizarmos alguma tabela, o banco de dados manterá os registros e será modificado.

Basicamente é isso, por padrão o Spring irá definir a maioria das configurações e isso é o suficiente para este projeto. Já conseguimos rodar o projeto e o acessar em [http://localhost:8080](http://localhost:8080/). (Sim, ainda não definimos nenhum end-point e o retorno não será amigável.

### Criando uma Model

Agora vamos criar a nossa domain para armazenar os dados do Portador. Crie um package chamado `model` em `com.pitzdev.sandbox`. Em model, crie um package chamado `base`. E dentro da base, crie um package chamado `holder`. (Eu particularmente gosto dessa organização, não criando os arquivos na pasta raiz)

É comum, em um projeto real, utilizarmos herança para herdar atributos padrões na aplicação. Por exemplo, digamos que toda tabela deve possuir a data de cadastro do registro. Para isso, iremos definir a classe `BaseEntity` dentro do package `base`.

`BaseEntity`

    package com.pitzdev.sandbox.model.base
    
    import org.hibernate.annotations.CreationTimestamp
    import org.hibernate.annotations.UpdateTimestamp
    import java.time.Instant
    import javax.persistence.*
    
    @MappedSuperclass
    open class BaseEntity (
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id : Long? = null,
    
        @Version
        val version: Long? = null,
    
        @field:CreationTimestamp
        val dateCreated: Instant? = null,
    
        @field:UpdateTimestamp
        val lastUpdated: Instant? = null,
    		
    		var deleted: Boolean = false
    )

Eu defini alguns campos padrões para as entidades dessa aplicação, são eles: Id, Version, DateCreated e LastUpdated. (Bom, eu não sei se a utilização do `open class` seria a melhor opção para criar uma classe base para entidades. Entretanto, foi uma solução que funcionou)

A annotation `@MappedSuperclass` permite que outra entidade herde o mapeamento de colunas de uma classe concreta ou abstrata. A classe que não é uma entidade, ou seja, não tem a anotação `@Entity`, não tem uma relação direta com uma tabela e por isso mesmo não pode ser utilizada em queries.

Agora que a nossa `BaseEntity` foi criada, está na hora de definirmos a entidade `Holder`. Para isso, crie a classe dentro do package `holder`.

    package com.pitzdev.sandbox.model.holder
    
    import com.pitzdev.sandbox.model.base.BaseEntity
    import java.util.*
    import javax.persistence.Entity
    import javax.persistence.GeneratedValue
    import javax.persistence.GenerationType
    import javax.validation.constraints.NotNull
    
    @Entity
    data class Holder (
    
            @get: NotNull
            var name: String = "",
    
            @get: NotNull
            var cpfCnpj: String = "",
    
            @get: NotNull
            @GeneratedValue(strategy = GenerationType.SEQUENCE)
            var publicId: String = "",
    
            var externalId: String?,
    
            var birthDate: Date?,
    
            var email: String = "",
    
            var phone: String?,
    
            var mobilePhone: String?,
    
            var address: String = "",
    
            var addressNumber: Int?,
    
            var complement: String?,
    
            var postalCode: String = "",
    
            var province: String = "",
    
            var city: String = "",
    
            var state: String = "",
    
            var motherName: String = ""
    
    ) : BaseEntity()

 

Sem segredo, a classe Holder irá herdar os campos definidos na classe `BaseEntity`. Uma `data class` possui ainda algumas cartas na manga que você pode ver aqui. [https://kotlinlang.org/docs/reference/data-classes.html](https://kotlinlang.org/docs/reference/data-classes.html)

### Criando um Repository

Com a nossa Model definida, iremos criar um Repository que será responsável por acessar os dados do banco de dados. Primeiro, crie um package chamado `repository` em `com.pitzdev.sandbox`. Dentro desse package, crie um novo arquivo chamado `HolderRepository.kt`.

    package com.pitzdev.sandbox.repository
    
    import com.pitzdev.sandbox.model.holder.Holder
    import org.springframework.data.repository.CrudRepository
    import org.springframework.stereotype.Repository
    
    @Repository
    interface HolderRepository : CrudRepository<Holder, Long>

Isso é tudo por enquanto. A classe CrudRepository contém todos os métodos CRUD que iremos precisar por hora.

### Criando um Service

Bom, antes de criarmos o Controller, vamos criar um Service para armazenar toda a lógica de negócio. Assim, com a camada Service, mantemos a camada Controller com um objetivo mais sucinto: Saber manipular um JSON e controlar o acesso dos usuários.

Para criar o Service:

1. Criar um package chamado `service` em `com.pitzdev.sandbox`.
2. Em `service`, criar um package chamado `holder`.
3. Em `holder`, criar um arquivo chamado `HolderService.kt`.

    package com.pitzdev.sandbox.services
    
    import com.pitzdev.sandbox.repository.HolderRepository
    import com.pitzdev.sandbox.model.holder.Holder
    import org.springframework.data.repository.findByIdOrNull
    import org.springframework.stereotype.Service
    
    @Service
    class HolderService(private val holderRepository: HolderRepository) {
    
        fun get(id: Long): Holder? {
            return holderRepository.findByIdOrNull(id)
        }
    
        fun list(): List<Holder> {
            return holderRepository.findAll() as List<Holder>
        }
    
        fun save(holder: Holder): Holder {
            if (holder.cpfCnpj.isEmpty()) setError("O campo CPF/CNPJ é obrigatório.")
            if (holder.name.isEmpty()) setError("O campo nome é obrigatório.")
    
            return holderRepository.save(holder)
        }
    
        fun delete(id: Long) {
            var holder = get(id)
    
            if (holder != null) {
                holder.deleted = true
                holderRepository.save(holder)
            } else {
                setError("O portador informado não existe.")
            }
        }
    
        fun update(id: Long, holder: Holder): Holder? {
            var currentHolder = get(id)
    
            if (currentHolder != null) {
                currentHolder.phone = holder.phone
                currentHolder.email = holder.email
                currentHolder.address = holder.address
                currentHolder.addressNumber = holder.addressNumber
                currentHolder.city = holder.city
                currentHolder.complement = holder.complement
                holderRepository.save(currentHolder)
            } else {
                setError("O portador informado não existe.")
            }
    
            return currentHolder
        }
    
        private fun setError(message: String): Unit = throw IllegalArgumentException(message)
    }

// To do - Finalizar documentação.

### Considerações Parciais

- Bom, o Kotlin é uma linguagem boa para codar. O IntelliJ me ajuda um bocado o desenvolvedor, me lembra muito do Visual Studio.
- A nullability do Kotlin também é uma feature interessante, evita cagadas no código.
- Para quem vem do Java ou Groovy, a curva de aprendizado não parece algo muito complicado. Tenho tido algumas dúvidas pequenas e sinto dificuldade em materiais focados em server-side applications.
- A hype vai morrer?

Esse projeto usa como base o artigo [https://www.callicoder.com/kotlin-spring-boot-mysql-jpa-hibernate-rest-api-tutorial/](https://www.callicoder.com/kotlin-spring-boot-mysql-jpa-hibernate-rest-api-tutorial/)
