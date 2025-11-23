## INTEGRANTES:
    André Luiz Hadlich Fidelis Junior
    Caio Henrique Fernandes
    Jean Ernani Witt Meier
    José Augusto Fernandes
    Lindaura Mariana Wanka

# Vídeo do projeto
https://www.youtube.com/watch?v=U6M_l34Af7A

# ⚙️ Procedimentos de Build e Execução

## 1. Visão Geral das Configurações do Ambiente

O projeto foi desenvolvido e testado com as seguintes versões e configurações de ambiente. É fundamental que o ambiente de execução as siga o mais próximo possível para garantir a reprodutibilidade.

| **Android Gradle Plugin (AGP)**   | `7.3.1`                   | Configurado no arquivo `build.gradle` (Project). |
| **Gradle Version**                | `8.5`                     | Necessário para o sistema de automação do build. |
| **Gradle JDK**                    | `Microsoft OpenJDK 17`    | Versão do Java Development Kit utilizada para rodar o Gradle. |
| **`compileSdk`**                  | `32`                      | API 32 (Android 12L) utilizada para compilação. |
| **`targetSdk`**                   | `32`                      | API 32 (Android 12L) alvo para execução. |

### Configuração Crucial no `build.gradle` (Module :app)

Para evitar falhas de build devido a conflitos de arquivos internos em dependências (como `META-INF/DEPENDENCIES`), a seguinte configuração de `packagingOptions` foi adicionada e é **obrigatória**:

android {
    // ...
    packagingOptions {
        exclude 'META-INF/DEPENDENCIES'
    }
}

**O dispositivo emulado em que foi rodada a aplicação está como um "Medium Phone API 36.1 Android 16.0 ("Baklava") | x86_64".**

**Informações adicionais: o sistema operacional que foi utilizado para o desenvolvimento da aplicação foi o Microsoft Windows 11**

### Informações sobre o banco de dados utilizado
Foi utilizado o SQLite, todas as informações sobre o banco e as tabelas utilizadas estão na pasta `app/src/main/java/com/example/myapplication/database/DatabaseHelper.java`
As tabelas ficaram organizadas da seguinte forma
# -> para chave primária & -> para chave estrangeira
    filmes  (#idFilme, nome, duracao, genero, classificacao);
    sessoes (#idSessao, hora, data, &idLocal, &idFilme);
    locais  (#idLocal, sala, bloco);

### Explicações sobre o funcionamento do aplicativo
DOCUMENTAÇÃO DO SISTEMA - PARTE 1: AUTENTICAÇÃO E NAVEGAÇÃO
PROJETO: CINEMA ESTUDANTIL

1. MÓDULO DE LOGIN (MainActivity.java)
-----------------------------------------------------------------------
Responsável pela autenticação inicial do usuário no sistema. Esta é a
primeira tela apresentada ao abrir o aplicativo.

Funcionalidades:
A. Inicialização de Componentes:
   - Carrega o layout de login.
   - Mapeia os campos de entrada de texto (E-mail e Senha) e o botão
     de ação.

B. Validação de Dados:
   - Verifica se os campos de e-mail ou senha estão vazios antes de
     processar.
   - Exibe feedback visual (Toast) solicitando o preenchimento caso
     algum campo esteja em branco.

C. Autenticação:
   - Realiza a verificação das credenciais inseridas.
   - Credenciais atuais (Hardcoded para protótipo):
     * Usuário: admin@email.com
     * Senha:   123456
   - Em caso de falha: Exibe mensagem de "E-mail ou senha incorretos".

D. Navegação:
   - Em caso de sucesso: Redireciona o usuário para o Menu Principal
     (MenuAdm).
   - Encerra a atividade de Login (finish()) para impedir que o botão
     "Voltar" retorne à tela de autenticação após o acesso.


2. MÓDULO DE MENU ADMINISTRATIVO (MenuAdm.java)
-----------------------------------------------------------------------
Atua como o hub central de navegação do sistema, permitindo ao
administrador acessar as funcionalidades de gerenciamento (CRUDs).

Funcionalidades:
A. Gerenciamento de Sessões (Botão "Sessões"):
   - Redireciona para a tela de agendamento e listagem de sessões
     (CrudSessaoActivity).

B. Gerenciamento de Filmes (Botão "Filmes"):
   - Redireciona para a tela de cadastro e controle do catálogo de
     filmes (CrudFilmeActivity).

C. Gerenciamento de Locais (Botão "Locais"):
   - Redireciona para a tela de administração de salas e blocos
     (CrudLocalActivity).

D. Logout (Botão "Sair"):
   - Encerra a sessão atual do administrador.
   - Redireciona o usuário de volta para a tela de Login (MainActivity).
   - Garante o fechamento da pilha de navegação do menu.

DOCUMENTAÇÃO DO SISTEMA - PARTE 2: GERENCIAMENTO DE ENTIDADES (CRUDS)
PROJETO: CINEMA ESTUDANTIL

3.0. ARQUITETURA DE GERENCIAMENTO
-----------------------------------------------------------------------
Todas as telas de gerenciamento (CRUD) seguem um padrão arquitetural
consistente:
- Persistência: Utiliza SQLite via objetos DAO (Data Access Object).
- Threads: Todas as operações de banco de dados (inserção, listagem,
  exclusão) são executadas em Background Threads através de AsyncTasks
  para garantir que a interface do usuário (UI) não seja bloqueada.
- UI/UX: Utiliza RecyclerView para listagem e um Modal Card (overlay)
  para as operações de Inserção/Edição.

3.1. GESTÃO DE FILMES (CrudFilmeActivity.java)
-----------------------------------------------------------------------
Responsável pelo cadastro, listagem e exclusão de filmes do catálogo.

A. Listagem (Read):
   - A tarefa CarregarFilmesTask busca todos os filmes no FilmeDAO.
   - O adaptador (FilmeAdapter) exibe o título, gênero, duração e
     classificação na RecyclerView.
   - Exibe o 'Empty State' (Estado Vazio) caso não haja filmes
     cadastrados.

B. Cadastro (Create) - Modal:
   - Acionado pelo FAB (ExtendedFloatingActionButton).
   - Validação: Verifica se os campos 'Título' e 'Duração' estão
     preenchidos. Valida se 'Duração' e 'Classificação' são números.
   - A tarefa SalvarFilmeTask envia o novo objeto FilmeModel ao DAO.
   - Regra de Negócio: Se o filme já existir (título duplicado, retorno
     -2 do DAO), exibe um Toast de erro específico ("Filme já existe!").

C. Exclusão (Delete):
   - A tarefa ExcluirFilmeTask executa a exclusão no DAO.
   - Regras de Negócio (Integridade Referencial):
     - Se o DAO retornar -2, significa que o filme está associado a uma
       sessão existente. Exibe um Toast de erro específico: "Não é
       possível excluir um filme associado a uma sessão existente."
     - Se retornar 1, a exclusão foi bem-sucedida e a lista é recarregada.


3.2. GESTÃO DE LOCAIS (CrudLocalActivity.java)
-----------------------------------------------------------------------
Responsável pelo cadastro de salas e blocos.

A. Listagem (Read):
   - A tarefa CarregarLocaisTask busca os locais no LocalDAO e popula
     a RecyclerView.
   - O adaptador (LocalAdapter) exibe o Bloco e o número da Sala.

B. Cadastro (Create) - Modal:
   - Validação: Verifica se Sala e Bloco estão preenchidos. Valida se
     'Sala' é um número inteiro.
   - A tarefa SalvarLocalTask insere o LocalModel no DAO.
   - Regra de Negócio: Se o Local já existir (sala e bloco duplicados,
     retorno -2 do DAO), exibe um Toast de erro ("Local já cadastrado!").

C. Exclusão (Delete):
   - A tarefa ExcluirLocalTask executa a exclusão no DAO.
   - Regras de Negócio (Integridade Referencial):
     - Se o DAO retornar -2, significa que o local está associado a uma
       sessão existente. Exibe um Toast de erro: "Não é possível
       excluir um local associado a uma sessão existente."
     - Se retornar 1, a exclusão foi bem-sucedida e a lista é recarregada.


3.3. GESTÃO DE SESSÕES (CrudSessaoActivity.java)
-----------------------------------------------------------------------
Responsável por agendar filmes em locais e horários específicos.

A. Listagem e Pré-Carregamento (Read):
   - A tarefa CarregarDadosTask é fundamental, pois carrega:
     1. Todos os Filmes (para o Spinner).
     2. Todos os Locais (para o Spinner).
     3. Todas as Sessões (para o RecyclerView).
   - O adaptador (SessaoAdapter) consulta as listas de filmes
     e locais para exibir o Título do Filme e a descrição do Local, em
     vez de apenas os IDs.

B. Cadastro (Create) - Modal:
   - Regra de Pré-Requisito: Antes de abrir o modal, verifica-se se há
     pelo menos um Filme E um Local cadastrados. Se não houver, exibe
     o aviso "Você precisa cadastrar ao menos um FILME e um LOCAL antes!".
   - Spinners: Os dados dos Spinners são preenchidos com os objetos
     FilmeModel e LocalModel carregados em memória.
   - Validação: Verifica se Data e Hora estão preenchidos. Valida o
     formato da data (DD/MM/AAAA).
   - A tarefa SalvarSessaoTask envia o SessaoModel ao DAO.
   - Regra de Negócio: O SessaoDAO contém a validação de duplicidade
     por horário/local/data. Se duplicado, retorna -1 e exibe Toast.

C. Exclusão (Delete):
   - A tarefa ExcluirSessaoTask executa a exclusão.
   - Regra de Negócio: O SessaoDAO garante a exclusão e atualiza a
     RecyclerView, exibindo feedback de sucesso ou erro.

DOCUMENTAÇÃO DO SISTEMA - PARTE 2: ARQUITETURA E PERSISTÊNCIA
PROJETO: CINEMA ESTUDANTIL

2. ARQUITETURA DO SISTEMA DE DADOS
-----------------------------------------------------------------------
O sistema utiliza um banco de dados local SQLite no Android para persistência de dados. A arquitetura segue o padrão DAO (Data Access Object) para isolar a lógica de acesso ao banco do restante da aplicação.

2.1. CONFIGURAÇÃO DO BANCO (DatabaseHelper.java)
- Nome do Banco: cinema.db
- Versão: 1
- Habilitação de FKs: As chaves estrangeiras (Foreign Keys) são explicitamente habilitadas no método onConfigure, garantindo a integridade referencial ao nível do banco de dados (embora a lógica de checagem também esteja implementada nos DAOs).

2.2. ESTRUTURA E RELACIONAMENTO DE TABELAS O banco de dados possui três tabelas principais, relacionadas por um modelo N:1 (Muitos para Um):

### TABELA: FILMES
Armazena as informações do catálogo de filmes.
- COLUNAS:
    - idFilme (PK): INTEGER PRIMARY KEY AUTOINCREMENT
    - nome: TEXT
    - duracao: INTEGER
    - genero: TEXT
    - classificacao: TEXT

### TABELA: LOCAIS
Armazena as informações sobre as salas/blocos disponíveis.
- COLUNAS:
    - idLocal (PK): INTEGER PRIMARY KEY AUTOINCREMENT
    - Sala: TEXT (Armazena o número da sala)
    - Bloco: TEXT

### TABELA: SESSOES
Tabela principal que agenda um filme em um local em um horário específico. Possui duas chaves estrangeiras (FKs).
- COLUNAS:
    - idSessao (PK): INTEGER PRIMARY KEY AUTOINCREMENT
    - hora: TEXT
    - data: TEXT
    - idLocal (FK): INTEGER (Referencia LOCAIS)
    - idFilme (FK): INTEGER (Referencia FILMES)

- RELACIONAMENTOS:
    - A tabela SESSOES depende de FILMES (Muitas sessões para Um filme).
    - A tabela SESSOES depende de LOCAIS (Muitas sessões para Um local).

2.3. LÓGICA DE ACESSO A DADOS (DAOs)
Cada entidade possui um DAO dedicado que encapsula a lógica SQL, mantendo a aplicação organizada e limpa.

### FilmeDAO.java
- Funcionalidades: Inserção, Listagem, e Exclusão.
- Regra de Negócio Implementada:
    - Duplicidade: Verifica se já existe um filme com o mesmo nome (titulo) antes de inserir.
    - Integridade: Antes de excluir, o método isFilmeVinculadoSessao checa se o idFilme existe na tabela SESSOES. Se existir, retorna -2, bloqueando a exclusão.

### LocalDAO.java
- Funcionalidades: Inserção, Listagem e Exclusão.
- Regra de Negócio Implementada:
    - Duplicidade: Verifica se já existe um local com a mesma combinação de Sala e Bloco antes de inserir.
    - Integridade: Antes de excluir, o método isLocalVinculadoSessao checa se o idLocal existe na tabela SESSOES. Se existir, retorna -2, bloqueando a exclusão.

### SessaoDAO.java
- Funcionalidades: Inserção, Listagem, e Exclusão.
- Regra de Negócio Implementada:
    - Duplicidade: O método isSessaoDuplicada verifica se já existe uma sessão para o mesmo Local (idLocal), na mesma Data e no mesmo Horário antes de inserir. Se for duplicada, retorna -1, impedindo a criação.
    - Exclusão: Permite a exclusão direta da sessão, pois ela não possui entidades dependentes.

DOCUMENTAÇÃO DO SISTEMA - PARTE 3: MODELOS DE DADOS (MODELS)
PROJETO: CINEMA ESTUDANTIL

Os modelos (Models) são classes Java que representam as entidades do mundo real e a estrutura das tabelas no banco de dados. Eles são responsáveis por encapsular os dados e fornecer métodos de acesso (getters e setters).

1. MODELO DE FILME (FilmeModel.java)
-----------------------------------------------------------------------
Representa a entidade FILMES no banco de dados e as informações sobre um filme no catálogo.

Atributos Principais:
- id_filme: INTEGER (Chave Primária, usada para identificação única).
- titulo: STRING
- genero: STRING
- duracao: INTEGER (Em minutos).
- classificacao: INTEGER (Idade mínima).

Funcionalidade de Construtores:
- O primeiro construtor é usado para inserção, pois não requer o id_filme (que é gerado automaticamente pelo banco).
- O segundo construtor (com id) é usado na leitura e listagem dos filmes a partir do banco.

Método toString():
- Retorna o título do filme. Usado em contextos onde o objeto é exibido diretamente, como em logs ou adaptadores básicos (embora a CrudSessaoActivity use o getTitulo() explicitamente).

2. MODELO DE LOCAL (LocalModel.java)
-----------------------------------------------------------------------
Representa a entidade LOCAIS no banco de dados e as informações sobre uma sala ou bloco.

Atributos Principais:
- id_local: INTEGER (Chave Primária).
- sala: INTEGER (Número da sala)
- bloco: STRING (Identificação do bloco, ex: "A", "B").

Funcionalidade de Construtores:
- Possui um construtor para inserção (sem id) e um construtor completo para leitura (com id).

Método toString():
- Retorna uma string formatada contendo o Bloco e o número da Sala (ex: "Bloco: A | Sala: 101"). Este formato é crucial para preencher o Spinner de Locais na CrudSessaoActivity.

3. MODELO DE SESSÃO (SessaoModel.java)
-----------------------------------------------------------------------
Representa a entidade SESSOES no banco de dados e o agendamento de um filme em um local e horário específicos. Esta classe lida com as Chaves Estrangeiras e a conversão de datas.

Atributos Principais:
- id_sessao: INTEGER (Chave Primária).
- data: java.util.Date (Armazena a data da sessão no formato de objeto Java).
- hora: STRING
- local: INTEGER (Chave Estrangeira - FK para idLocal)
- filme: INTEGER (Chave Estrangeira - FK para idFilme)

Métodos Auxiliares de Data:
- getDataString(): Converte o objeto java.util.Date para uma String no formato 'yyyy-MM-dd'. Este formato é padrão SQL e é utilizado para salvar a data no banco de dados.
- stringToDate(String dataString): Método estático que converte a string do banco ('yyyy-MM-dd') de volta para um objeto java.util.Date. Essencial ao ler os dados do banco para carregar o modelo.

Método toString():
- Retorna um resumo completo da sessão, incluindo data formatada (dd/MM/yyyy) e IDs de Local e Filme. Usado principalmente para depuração.