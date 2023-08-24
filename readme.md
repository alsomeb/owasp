# Exercise week 1, Grupp 3

## Uppgift: OWASP Top 10 Sårbarhetsutmaning för Java-utvecklare
### Syfte:
Att få en djupare förståelse för säkerhetsrisker inom webbutveckling genom att skapa och sedan åtgärda
exempel på två vanliga sårbarheter enligt OWASP Top 10 (2021).

### Valda Sårbarheter från OWASP Top 10
- Cryptographic Failure (Nr 2)
- Injection (Nr 3)


### Todo Cryptographic Failure
 - Kryptera Lösenorden i config filen, får ej läcka ut info
 - Kryptera Lösenorden i databasen mha tex BcryptEncoder
 - Skriva Rapport om sårbarheterna (CWE) och samt lösningen

### CWE's
- CWE-261 Weak Encoding for Password
- CWE-319 Cleartext Transmission of Sensitive Information
- CWE-259: Use of Hard-coded Password
- CWE-89 Improper Neutralization of Special Elements used in an SQL Command ('SQL Injection')
- CWE-564 SQL Injection: Hibernate
- CWE-20 Improper Input Validation
