### Building
`mvn clean install`

### Running
- `cd <project-to-analyze/>`
- `mvn se.kth:mvssc-maven-plugin:1.0-SNAPSHOT:mvssc -DcreateResultJson=true -DcreatePomDebloated=true -DlibraryToSpecialize=<artifactId>`
