# 🧊 Geladeira Inteligente

Aplicação web para organizar os alimentos da geladeira, acompanhar validades e gerar receitas personalizadas com o Chef IA.

O projeto combina um CRUD de alimentos com uma interface responsiva, modo escuro, identidade visual própria e integração com a API compatível com OpenAI da Groq.

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-ef6c43)
![Java](https://img.shields.io/badge/Java-21-25382d)
![Spring%20Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6d7f57)
![License](https://img.shields.io/badge/license-MIT-6d7f57)

## ✨ Funcionalidades

- Cadastro, edição e exclusão de alimentos.
- Categorias de alimentos com validação no frontend e backend.
- Controle de quantidade e data de validade.
- Alertas visuais para alimentos próximos do vencimento ou vencidos.
- Geração de receitas com base nos ingredientes cadastrados.
- Fallback automático para modelos alternativos da Groq.
- Mensagens de erro específicas em JSON, em vez de respostas genéricas `400`.
- Interface responsiva para desktop, tablet e celular.
- Modo claro/escuro persistido no navegador.
- Logo, favicon e identidade visual minimalista do projeto.
- Banco H2 em memória com migrations Flyway.

## 🖥️ Visão geral

O fluxo principal é:

1. Acesse a aplicação no navegador.
2. Cadastre os alimentos disponíveis.
3. Acompanhe quantidade e validade no inventário.
4. Clique em **Criar minha receita**.
5. O Chef IA usará os nomes dos alimentos cadastrados para sugerir uma receita.

## 🧱 Tecnologias

| Camada | Tecnologia |
|---|---|
| Backend | Java 21, Spring Boot 4.1.0 |
| API web | Spring MVC, Spring WebFlux |
| Persistência | Spring Data JPA, Hibernate |
| Banco de dados | H2 em memória |
| Migrations | Flyway |
| IA | Groq API com endpoint compatível com OpenAI |
| Frontend | HTML, CSS e JavaScript |
| Template | Thymeleaf |
| Build | Maven Wrapper |

## 📋 Pré-requisitos

- Java 21 ou superior.
- Uma chave de API da Groq.
- Internet para gerar receitas com IA.

O Maven não precisa ser instalado separadamente: o projeto inclui o Maven Wrapper (`mvnw` e `mvnw.cmd`).

## 🚀 Executando localmente

### 1. Clone o projeto

```bash
git clone https://github.com/pedrohenriquefalcao/geladeira-inteligente.git
cd geladeira-inteligente
```

### 2. Configure a chave da Groq

Defina a variável de ambiente `GROQ_API_KEY`.

No PowerShell:

```powershell
$env:GROQ_API_KEY="gsk_sua_chave_aqui"
```

No macOS/Linux:

```bash
export GROQ_API_KEY="gsk_sua_chave_aqui"
```

O Spring também aceita `API_KEY` como fallback, mas `GROQ_API_KEY` é a configuração recomendada.

Opcionalmente, personalize o modelo:

```powershell
$env:LLM_API_MODEL="openai/gpt-oss-20b"
$env:LLM_API_FALLBACK_MODELS="llama-3.1-8b-instant"
```

> Não versione chaves de API. Arquivos `.env` não são carregados automaticamente pelo Spring Boot; use variáveis de ambiente ou um mecanismo de configuração local apropriado.

### 3. Inicie a aplicação

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

Abra <http://localhost:8080>.

Para gerar o pacote:

```bash
# Windows
.\mvnw.cmd clean package

# macOS/Linux
./mvnw clean package
```

## ⚙️ Configuração

As configurações padrão estão em [`application.properties`](./src/main/resources/application.properties):

```properties
server.port=${PORT:8080}
spring.datasource.url=jdbc:h2:mem:geladeiradb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
llm.api.url=https://api.groq.com/openai/v1
llm.api.model=${LLM_API_MODEL:openai/gpt-oss-20b}
llm.api.fallback-models=${LLM_API_FALLBACK_MODELS:llama-3.1-8b-instant}
GROQ_API_KEY=${GROQ_API_KEY:}
```

Para alterar a porta:

```powershell
$env:PORT="8081"
.\mvnw.cmd spring-boot:run
```

## 🗄️ Banco H2

O banco atual é criado em memória e perde os dados quando a aplicação é encerrada.

Console H2:

- URL: <http://localhost:8080/h2-console>
- JDBC URL: `jdbc:h2:mem:geladeiradb`
- Usuário: `sa`
- Senha: deixe em branco

O console está configurado em [`H2ConsoleConfig.java`](./src/main/java/com/independente/geladeira_inteligente/config/H2ConsoleConfig.java).

## 📡 API REST

A API de alimentos usa o prefixo `/alimento`.

### Criar alimento

```http
POST /alimento/salvar
Content-Type: application/json
```

```json
{
  "nome": "Maçã Fuji",
  "categoria": "FRUTAS",
  "quantidade": 5,
  "validade": "2026-11-15"
}
```

### Listar alimentos

```http
GET /alimento/listar/todos
```

### Buscar alimento por ID

```http
GET /alimento/listar/{id}
```

### Atualizar alimento

```http
PUT /alimento/alterar/{id}
Content-Type: application/json
```

```json
{
  "nome": "Maçã Gala",
  "categoria": "FRUTAS",
  "quantidade": 3,
  "validade": "2026-11-20"
}
```

### Excluir alimento

```http
DELETE /alimento/deletar/{id}
```

### Gerar receita

```http
POST /receita/gerar
```

O endpoint usa os alimentos cadastrados. Não recebe prompt no corpo da requisição.

Se não houver alimentos, a resposta será:

```text
Sua geladeira está vazia! Adicione ingredientes antes de pedir uma receita.
```

## 🏷️ Categorias aceitas

Os valores devem ser enviados exatamente como aparecem abaixo:

```text
BEBIDAS
LATICINIOS
HORTIFRUTI
CARNES_E_PEIXES
EMBUTIDOS
CONGELADOS
MOLHOS_E_CONDIMENTOS
SOBREMESAS
OVOS
SOBRAS
GRAOS
FRUTAS
```

### Regras de validação

- `nome`: obrigatório, com pelo menos 2 caracteres.
- `categoria`: obrigatória e deve existir no enum.
- `quantidade`: número inteiro entre 1 e 999.
- `validade`: data posterior à data atual.

## ❌ Respostas de erro

Erros de validação retornam JSON com código e mensagem específicos:

```json
{
  "timestamp": "2026-10-29T20:00:00",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "A quantidade deve estar entre 1 e 999."
}
```

Outros códigos usados pela API:

| Código | Status | Significado |
|---|---:|---|
| `VALIDATION_ERROR` | 400 | Dados do alimento não atendem às regras de negócio |
| `INVALID_REQUEST_BODY` | 400 | JSON inválido ou categoria desconhecida |
| `INVALID_PARAMETER` | 400 | Parâmetro de rota com formato inválido |
| `NOT_FOUND` | 404 | Alimento não encontrado |

## 🧪 Testes e validação

Executar os testes:

```powershell
.\mvnw.cmd test
```

Compilar sem executar os testes:

```powershell
.\mvnw.cmd -DskipTests package
```

Validar a sintaxe do frontend:

```powershell
node --check .\src\main\resources\static\js\home.js
```

## 📁 Estrutura do projeto

```text
src/
├── main/
│   ├── java/com/independente/geladeira_inteligente/
│   │   ├── config/                 # Configurações do Spring e WebClient
│   │   ├── controller/             # Rotas HTML e endpoints REST
│   │   ├── dto/                    # Objetos de integração com a IA
│   │   ├── enums/                  # Categorias de alimentos
│   │   ├── exception/              # Erros e respostas padronizadas da API
│   │   ├── model/                  # Entidades JPA
│   │   ├── repository/             # Repositórios
│   │   └── service/                # Regras de negócio e integração com IA
│   └── resources/
│       ├── db/migration/            # Scripts Flyway
│       ├── static/
│       │   ├── brand/               # Logos e favicon
│       │   ├── css/                 # Estilos da aplicação
│       │   └── js/                  # Comportamento da interface
│       ├── templates/               # Páginas Thymeleaf
│       └── application.properties
└── test/                            # Testes automatizados
```

## 🎨 Identidade visual

O projeto utiliza uma identidade minimalista baseada em:

- Laranja `#EF6C43`;
- Verde `#6D7F57`;
- Verde escuro `#25382D`;
- Fundo neutro `#F7F5F0`.

As variações de logo estão em [`src/main/resources/static/brand`](./src/main/resources/static/brand), incluindo o [showcase](./src/main/resources/static/brand/logo-showcase.html).

## 🛠️ Troubleshooting

### `GROQ_API_KEY` não configurada

Defina a variável no mesmo terminal em que a aplicação será iniciada. Sem ela, o CRUD continua disponível, mas a geração de receitas não poderá chamar a IA.

### Erro `Categoria inválida`

Use os valores do enum, com sublinhados. Por exemplo, envie `CARNES_E_PEIXES`, não `CARNES E PEIXES`.


### H2 não conecta

Confirme que a aplicação está em execução e use exatamente:

- JDBC URL: `jdbc:h2:mem:geladeiradb`;
- Usuário: `sa`;
- Senha vazia.

### A receita não é gerada

Confira:

1. Se há pelo menos um alimento cadastrado;
2. Se `GROQ_API_KEY` está configurada;
3. Se a aplicação tem acesso à internet;
4. Se o modelo principal ou fallback está disponível.

## 🤝 Contribuindo

1. Faça um fork do projeto.
2. Crie uma branch:

   ```bash
   git checkout -b feature/minha-melhoria
   ```

3. Faça as alterações e valide o projeto.
4. Crie um commit descritivo.
5. Abra um Pull Request.

## 📄 Licença

Este projeto está sob a licença MIT.

