# 🧊 Geladeira Inteligente

Um sistema inteligente de gerenciamento de estoque de geladeira que utiliza **inteligência artificial (ChatGPT)** para gerar receitas baseadas nos alimentos disponíveis. O projeto combina um backend robusto com gerenciamento de inventário e recomendações culinárias inteligentes.

---

## ✨ Funcionalidades Principais

- 🥘 **Gerenciamento de Alimentos** - Adicione, edite e remova itens com informações detalhadas
- 🤖 **Geração de Receitas com IA** - Utilize ChatGPT para gerar receitas baseadas nos alimentos da geladeira
- 📊 **Categorização de Alimentos** - Organize alimentos por categoria
- 📅 **Controle de Validade** - Rastreie datas de expiração dos alimentos
- 🗂️ **Banco de Dados em Tempo Real** - Persistência com H2 Database e JPA
- 🔄 **API RESTful** - Endpoints para integração fácil com front-end
- 📝 **Migrations com Flyway** - Controle de versão do banco de dados

---

## 🏗️ Arquitetura

### Stack Tecnológico

| Camada | Tecnologia |
|--------|-----------|
| **Backend** | Spring Boot 4.1.0 |
| **Linguagem** | Java 21 |
| **Banco de Dados** | H2 Database |
| **ORM** | Spring Data JPA + Hibernate |
| **IA** | OpenAI ChatGPT API |
| **Reactividade** | Spring WebFlux |
| **Build** | Maven |
| **Migrations** | Flyway |

### Estrutura do Projeto

```
src/main/java/com/independente/geladeira_inteligente/
├── config/              # Configurações (WebClient, Security)
├── controller/          # REST Controllers
├── dto/                 # Data Transfer Objects
├── enums/               # Enumerações
├── model/               # Modelos JPA
├── repository/          # Repositórios Data JPA
├── service/             # Lógica de negócio
└── GeladeiraInteligenteApplication.java

src/main/resources/
├── application.properties
└── db/migration/        # Scripts Flyway
```

---

## 📋 Pré-requisitos

- **Java 21+** instalado
- **Maven 3.6+** instalado
- **OpenAI API Key** (https://platform.openai.com/account/api-keys)

---

## 🚀 Instalação e Configuração

### 1. Clone o repositório

```bash
git clone https://github.com/pedrohenriquefalcao/geladeira-inteligente.git
cd geladeira-inteligente
```

### 2. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
API_KEY=sk-seu-token-openai-aqui
```

**Ou** configure no `application.properties`:

```properties
API_KEY=sk-seu-token-openai-aqui
chatgpt.api.url=https://api.openai.com/v1
```

### 3. Compile e execute o projeto

```bash
# Build do projeto
./mvnw clean install

# Executar o servidor
./mvnw spring-boot:run
```

O servidor iniciará em: **http://localhost:8080**

---

## 🗄️ Banco de Dados

### Acesso ao H2 Console

Abra seu navegador em: **http://localhost:8080/h2-console**

**Credenciais:**
- **JDBC URL**: `jdbc:h2:mem:geladeiradb`
- **Username**: `SMARTFRIDGE`
- **Password**: (deixe vazio)

---

## 📡 API REST - Endpoints

### 🍎 Alimentos (Food Items)

#### Adicionar novo alimento
```http
POST /food/salvar
Content-Type: application/json

{
  "nome": "Maçã",
  "categoria": "FRUTAS",
  "quantidade": 5,
  "dataValidade": "2026-08-13"
}
```

#### Listar todos os alimentos
```http
GET /food/listar/todos
```

#### Buscar alimento por ID
```http
GET /food/listar/{id}
```

#### Atualizar alimento
```http
PUT /food/alterar/{id}
Content-Type: application/json

{
  "nome": "Maçã Gala",
  "categoria": "FRUTAS",
  "quantidade": 3,
  "dataValidade": "2026-08-13"
}
```

#### Deletar alimento
```http
DELETE /food/deletar/{id}
```

---

### 🤖 Receitas (Recipes)

#### Gerar receita com ChatGPT
```http
GET /gerar-receita?prompt=Tenho maçã, leite e farinha. Faça uma receita
```

**Resposta:**
```json
{
  "receita": "Bolo de Maçã com Leite...",
  "ingredientes": [...],
  "modo_preparo": "..."
}
```

---

## 📊 Modelo de Dados

### ItemAlimentoModel

```java
@Entity
public class ItemAlimentoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;
    private CategoriaAlimento categoria;
    private Integer quantidade;
    private LocalDate dataValidade;
    private LocalDateTime criadoEm;
}
```

### CategoriaAlimento (Enum)

```
FRUTAS, VEGETAIS, PROTEÍNAS, LÁCTEOS, 
GRÃOS, BEBIDAS, CONDIMENTOS, OUTROS
```

---

## 🔧 Configuração Avançada

### Variáveis de Ambiente Disponíveis

```properties
# Banco de Dados
DATABASE_URL=jdbc:h2:mem:geladeiradb
DATABASE_USERNAME=SMARTFRIDGE
DATABASE_PASSWORD=

# OpenAI
API_KEY=sk-seu-token-aqui
chatgpt.api.url=https://api.openai.com/v1

# Servidor
server.port=8080

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

---

## 💡 Exemplos de Uso

### Exemplo 1: Adicionar alimento e gerar receita

```bash
# 1. Adicionar maçã
curl -X POST http://localhost:8080/food/salvar \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maçã",
    "categoria": "FRUTAS",
    "quantidade": 5,
    "dataValidade": "2026-08-13"
  }'

# 2. Gerar receita
curl "http://localhost:8080/gerar-receita?prompt=Tenho%205%20maçãs%20e%20leite"
```

### Exemplo 2: Listar todos os alimentos

```bash
curl http://localhost:8080/food/listar/todos
```

---

## 🐛 Troubleshooting

### Erro: "API_KEY não configurada"
**Solução**: Verifique se a variável `API_KEY` está definida no `.env` ou `application.properties`

### Erro: "H2 Console inacessível"
**Solução**: Certifique-se que:
- O servidor está rodando em `http://localhost:8080`
- Spring Security está configurado corretamente
- Use credenciais: `SMARTFRIDGE` / (vazio)

### Erro ao conectar com ChatGPT
**Solução**:
- Valide sua OpenAI API Key em https://platform.openai.com/account/api-keys
- Verifique internet e firewall
- Confirme que o endpoint é `https://api.openai.com/v1`

---

## 📚 Documentação Adicional

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [OpenAI API Docs](https://platform.openai.com/docs)
- [H2 Database](https://www.h2database.com/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)



---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Siga os passos:

1. Fork o repositório
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## 👨‍💻 Autor

**Pedro Henrique Falcão**
- GitHub: [@pedrohenriquefalcao](https://github.com/pedrohenriquefalcao)
- Email: pedrofalcaodev@gmail.com

---

## 📞 Suporte

Abra uma issue no repositório para reportar bugs ou sugerir melhorias!

---

<div align="center">

**Inspirado em:** Samsung Evolution RB50

[📺 Vídeo de Inspiração](https://www.youtube.com/watch?v=1cdXMZ4BI7M)

⭐ Se este projeto foi útil, deixe uma estrela!

</div>
