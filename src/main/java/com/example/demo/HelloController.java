package com.example.demo;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


@RestController
public class HelloController {
	String jdbcUrl = "jdbc:postgresql://localhost:5432/mydb";
    String username = "postgres";
    String password = "admin";


	@GetMapping("/list_user")
	public String list_user(@RequestHeader("Authorization") String accesstoken ) {

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("https://localhost:9443/scim2/Users"))
			.GET()
			.setHeader("accept", "application/scim+json")
			.setHeader("Authorization", accesstoken)
			.build();

		HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String return_value = response.body() ;
			 
		return return_value;
	}

	@GetMapping("/create_user")
	public String create_user(
		@RequestHeader("Authorization") String accessToken,
		@RequestParam String username,
		@RequestParam String password,
		@RequestParam String givenname,
		@RequestParam String lastname,
		@RequestParam String email,
		@RequestParam(required = false) String workmail,
		@RequestParam(required = false) String manager,
		@RequestParam(required = false) String employeenum
	) {
	
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("https://localhost:9443/scim2/Users"))
			.POST(BodyPublishers.ofString("{\n\"schemas\": [],\n\"name\": {\n  \"givenName\": \""+givenname+"\",\n  \"familyName\": \""+lastname+"\"\n},\n\"userName\": \""+username+"\",\n\"password\": \""+password+"\",\n\"emails\": [\n  {\n    \"value\": \""+email+"\",\n  },\n  {\n    \"type\": \"work\",\n    \"value\": \""+workmail+"\"\n  }\n],\n\"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\": {\n  \"employeeNumber\": \""+employeenum+"\",\n  \"manager\": {\n    \"value\": \""+manager+"\"\n  }\n}\n}"))
			.setHeader("accept", "application/scim+json")
			.setHeader("Authorization", "Bearer "+accessToken)
			.setHeader("Content-Type", "application/scim+json")
			.build();

		HttpResponse<String> response = null ;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            String insertQuery = "INSERT INTO employees (first_name, last_name) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, givenname);
            preparedStatement.setString(2, lastname);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

		String return_value = response.body() ;
		return return_value ;
	}
	
	@GetMapping("/delete_user")
	public String delete_user(
		@RequestHeader("Authorization") String accessToken,
		@RequestParam String userid
	) {

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("https://localhost:9443/scim2/Users/"+userid))
			.DELETE()
			.setHeader("accept", "*/*")
			.setHeader("Authorization", "Bearer "+accessToken)
			.build();

		HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String return_value = response.body() ;
		return return_value ;
	}
	
	@GetMapping("/search_user")
	public String search_user(
		@RegisteredOAuth2AuthorizedClient("wso2") OAuth2AuthorizedClient authorizedClient,
		@RequestParam String attributes,
		@RequestParam String filter,
		@RequestParam(defaultValue = "PRIMARY") String domain,
		@RequestParam(defaultValue = "1") String startindex,
		@RequestParam(defaultValue = "10") String count
	) {
		OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("https://localhost:9443/scim2/Users/.search"))
			.POST(BodyPublishers.ofString("{\n\"schemas\": [\n  \"urn:ietf:params:scim:api:messages:2.0:SearchRequest\"\n],\n\"attributes\": [\n  "+attributes+"\n],\n\"filter\": \""+filter+"\",\n\"domain\": \""+domain+"\",\n\"startIndex\": "+startindex+",\n\"count\": "+count+"\n}"))
			.setHeader("accept", "application/scim+json")
			.setHeader("Content-Type", "application/scim+json")
			.setHeader("Authorization", "Bearer "+accessToken.getTokenValue())
			.build();

		HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String return_value = response.body() ;
		return return_value ;
	}

	@GetMapping("/search_user_id")
	public String search_user_id(
		@RegisteredOAuth2AuthorizedClient("wso2") OAuth2AuthorizedClient authorizedClient,
		@RequestParam String userid
	) {
		OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("https://localhost:9443/scim2/Users/"+userid))
			.GET()
			.setHeader("accept", "application/scim+json")
			.setHeader("Authorization", "Bearer "+accessToken.getTokenValue())
			.build();

		HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String return_value = response.body() ;
		return return_value ;
	}

	@GetMapping("/put_update_user")
	public String put_update_user(
		@RegisteredOAuth2AuthorizedClient("wso2") OAuth2AuthorizedClient authorizedClient,
		@RequestParam String userid,
		@RequestParam String username,
		@RequestParam String password,
		@RequestParam String givenname,
		@RequestParam String lastname,
		@RequestParam String email,
		@RequestParam(required = false) String workmail,
		@RequestParam(required = false) String manager,
		@RequestParam(required = false) String employeenum
	) {
		OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("https://localhost:9443/scim2/Users/"+userid))
			.PUT(BodyPublishers.ofString("{\n\"schemas\": [],\n\"name\": {\n  \"givenName\": \""+givenname+"\",\n  \"familyName\": \""+lastname+"\"\n},\n\"userName\": \""+username+"\",\n\"password\": \""+password+"\",\n\"emails\": [\n  {\n    \"value\": \""+email+"\",\n  },\n  {\n    \"type\": \"work\",\n    \"value\": \""+workmail+"\"\n  }\n],\n\"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\": {\n  \"employeeNumber\": \""+employeenum+"\",\n  \"manager\": {\n    \"value\": \""+manager+"\"\n  }\n}\n}"))
			.setHeader("accept", "application/scim+json")
			.setHeader("Content-Type", "application/scim+json")
			.setHeader("Authorization", "Bearer "+accessToken.getTokenValue())
			.build();
		
		HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String return_value = response.body() ;
		return return_value ;
	}

	@GetMapping("/patch_update_user")
	public String patch_update_user(
		@RegisteredOAuth2AuthorizedClient("wso2") OAuth2AuthorizedClient authorizedClient,
		@RequestParam String userid,
		@RequestParam String operations
	) {
		OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("https://localhost:9443/scim2/Users/"+userid))
			.method("PATCH", BodyPublishers.ofString("{\n\"schemas\": [\n  \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n],\n\"Operations\": "+operations+"\n}"))
			.setHeader("accept", "application/scim+json")
			.setHeader("Content-Type", "application/scim+json")
			.setHeader("Authorization", "Bearer "+accessToken.getTokenValue())
			.build();
		
		HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String return_value = response.body() ;
		return return_value ;
	}


	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}
}