# Database Setup

Make sure the user that will be used to access the database has access to READ/WRITE and the database name is 'postgres'.

## Username and Password Values

Change the variables values `spring.datasource.username` and `spring.datasource.password` in `application.properties`.

### User Creation Example

```sql
CREATE USER username PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE postgres to username;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO username;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO username;
```

## Tables

- mensagens_springchat
```sql
CREATE TABLE mensagens_springchat (
    id SERIAL PRIMARY KEY,
    usuario VARCHAR (20) NOT NULL,
    data TIMESTAMP NOT NULL,
    mensagem VARCHAR (200) NOT NULL,
    canal VARCHAR (35) NOT NULL,
    ip VARCHAR (21) NOT NULL
);
```

- usuarios_springchat
```sql
CREATE TABLE usuarios_springchat (
    id SERIAL PRIMARY KEY,
    usuario VARCHAR (20) NOT NULL UNIQUE,
    senha VARCHAR (60) NOT NULL,
    data_criado TIMESTAMP NOT NULL,
    ip_criado VARCHAR (21) NOT NULL,
    roles VARCHAR(100) NOT NULL,
    conta_ativada boolean NOT NULL
);
```