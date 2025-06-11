# example-spring-boot-ai-app

## Local Development

1. Create an `.env` file in the root directory of the project.
2. Add the following environment variables to the `.env` file:

   ```
   OPENAI_API_KEY=sk-xxx
   ```
3. Create a maven profile with your OpenAI API key in the `pom.xml` file:

   ```xml
   <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
     <profiles>
       <profile>
         <id>openai-profile</id>
         <properties>
           <OPENAI_API_KEY>sk-xxx</OPENAI_API_KEY>
         </properties>
       </profile>
     </profiles>
     <activeProfiles>
       <activeProfile>openai-profile</activeProfile>
     </activeProfiles>
   </settings>
   ```
