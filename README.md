# SaveUp Back-end

Aplicação back-end do projeto **SaveUp**, foi desenvolvida utilizando Java com Quarkus. Este módulo é responsável pelas regras de negócio, persistência dos dados, autenticação, processamento das transações e geração de relatórios utilizados pelo sistema.

## Visão geral

O back-end fornece a API consumida pelo front-end em Angular e centraliza a lógica principal da aplicação. Entre suas responsabilidades estão o gerenciamento de usuários, controle de ganhos e gastos, cálculos totais financeiros e geração de relatórios em PDF.

## Tecnologias utilizadas

- Java
- Quarkus
- JPA / Hibernate
- Maven
- PostgreSQL
- API REST
- OpenPDF.

## Funcionalidades principais

- Cadastro e autenticação de usuários.
- Persistência de dados financeiros.
- Gerenciamento de ganhos e gastos.
- Cálculo de totais, saldo e indicadores financeiros.
- Geração de relatórios em PDF.
- Histórico de relatórios gerados.
- Integração com o front-end via endpoints REST.

## Estrutura do projeto

```bash
## Estrutura do projeto

```bash
src/
 ├── main/
 │   ├── docker/
 │   ├── java/
 │   │   └── com/gastos/
 │   │       ├── dto/
 │   │       ├── model/
 │   │       ├── repository/
 │   │       ├── resource/
 │   │       ├── service/
 │   └── resources/
 │       ├── application.properties
 │       ├── privateKey.pem
 │       └── publicKey.pem
 ├── test/
 └── ...
```

## Como executar o projeto

### Pré-requisitos

- Java instalado.
- Maven instalado.
- PostgreSQL configurado.
- Variáveis e propriedades da aplicação ajustadas no `application.properties`.

### Instalação das dependências

```bash
mvn clean install
```

### Execução em ambiente de desenvolvimento

```bash
mvn quarkus:dev
```

## Build para produção

```bash
./mvnw package
```

## Banco de dados

O back-end utiliza PostgreSQL local para armazenar usuários, transações e registros relacionados ao sistema A configuração da conexão é feita pelo arquivo `application.properties` do Quarkus.

## API e integração com front-end

O front-end Angular consome a API REST do back-end para operações como autenticação, listagem de transações, geração de relatórios, download de PDFs e exclusão de históricos.

## Objetivo acadêmico

Este back-end faz parte do desenvolvimento desse meu TCC  e representa a camada de processamento e regras de negócio do sistema. Seu papel é garantir que os dados financeiros sejam tratados de forma consistente, segura e integrada à interface desenvolvida em Angular.

## Melhorias futuras

- Ampliar cobertura de testes.
- Refinar validações e tratamento de erros.
- Melhorar documentação dos endpoints.
- Evoluir autenticação e segurança.
- Expandir os tipos de relatórios e indicadores.
