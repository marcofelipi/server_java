# server_java

## Descrição do Projeto

Este projeto consiste no desenvolvimento de um servidor HTTP básico em Java, capaz de gerenciar múltiplas conexões de clientes simultaneamente através de threads. O servidor implementa as funcionalidades essenciais do protocolo HTTP, como os métodos GET e POST, e gerencia arquivos e sessões de usuários em memória, sem persistência em disco.

---

## Funcionalidades Implementadas

O servidor cumpre os seguintes requisitos principais:

- **Arquitetura Multithread:** Suporte a múltiplos clientes simultâneos utilizando Sockets TCP e Threads explícitas. [cite: 55]
- **Armazenamento em Memória:** Todos os arquivos e sessões de usuários são armazenados em estruturas de dados em memória (`HashMap`). [cite: 68]
- **Rota `GET /arquivos/{nome}`:** Permite a visualização de arquivos armazenados no servidor. [cite: 62] Retorna o conteúdo do arquivo com status `200 OK` ou um erro `404 Not Found` caso o arquivo não exista. [cite: 75]
- **Rota `POST /login`:** Implementa a autenticação de usuários. Recebe `username` e `password` via JSON e, em caso de sucesso, retorna uma chave de sessão (token) com status `200 OK`. [cite: 86, 87] Em caso de falha, retorna `403 Forbidden`. [cite: 89]
- **Rota `POST /arquivos`:** Permite a criação/atualização de novos arquivos. Esta rota é protegida e exige um token de autorização válido no cabeçalho `Authorization`. [cite: 78] Retorna `201 Created` em caso de sucesso ou `401 Unauthorized` se a autorização falhar. [cite: 79, 85]
- **Conformidade com HTTP:** Todas as mensagens trocadas entre cliente e servidor seguem o formato do protocolo HTTP, incluindo códigos de status e cabeçalhos como `Content-Type` e `Content-Length`. [cite: 70]

---

## Como Executar

1.  Abra o projeto em uma IDE Java compatível (ex: Apache NetBeans).
2.  Execute o método `main` da classe `ServidorHttpPrincipal.java`.
3.  O console indicará que o servidor está rodando na porta `8081`.

---

## Como Testar

### 1. Testando GET (Navegador)

- **Listar um arquivo existente:** Acesse `http://localhost:8081/arquivos/index.html`
- **Tentar listar um arquivo inexistente:** Acesse `http://localhost:8081/arquivos/nao_existe.txt` (deve retornar 404).

### 2. Testando o Fluxo de POST (Postman ou Insomnia)

#### Passo A: Fazer Login para Obter o Token

- **Método:** `POST`
- **URL:** `http://localhost:8081/login`
- **Body (raw, JSON):**
  ```json
  {
    "username": "admin",
    "password": "senha123"
  }
  ```
- **Resultado:** A resposta será um JSON com a sua chave de acesso. Copie essa chave.

#### Passo B: Criar um Novo Arquivo (com Autorização)

- **Método:** `POST`
- **URL:** `http://localhost:8081/arquivos`
- **Headers:**
  - `Authorization`: (cole a chave que você copiou do passo A)
- **Body (raw, JSON):**
  ```json
  {
    "nome": "teste.html",
    "conteudo": "<html><body><h1>Arquivo criado via POST!</h1></body></html>",
    "tipo": "text/html"
  }
  ```
- **Resultado:** A resposta deve ser `201 Created`.

#### Passo C: Verificar o Novo Arquivo

- No navegador, acesse `http://localhost:8081/arquivos/teste.html`. O conteúdo que você acabou de criar deve ser exibido.
