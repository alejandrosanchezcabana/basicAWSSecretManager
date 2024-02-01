package alex.examples;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.secretsmanager.caching.SecretCache;
import com.amazonaws.secretsmanager.caching.SecretCacheConfiguration;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.AWSSecretsManagerException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class Main {

  private static final String AWS_KEY = ""; //TODO: Fill value
  private static final String AWS_SECRET = ""; //TODO: Fill value
  private static final String SECRET_NAME = ""; //TODO: Fill value
  private static final String SECRET_KEY = ""; //TODO: Fill value
  private static final String REGION = "eu-west-1"; //TODO: Update region if needed

  public static void main(String[] args) {
    SecretCache secretCache = createSecretCache(100, 1000);
    try {
      String json = secretCache.getSecretString(SECRET_NAME);
      if (json == null) {
        System.out.println("JSON is null");
      }
      parseJSONAndGetValue(json, SECRET_KEY, SECRET_NAME);

    } catch (AWSSecretsManagerException ex) {
      System.out.println("Something went wrong" + ex);
    }
  }

  private static void parseJSONAndGetValue(String json, String key, String name) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> map = mapper.readValue(json, Map.class);
      String value = map.get(key) == null ? null : map.get(key).toString();
      if (value == null) {
        System.out.println("Value is null");
      }
      System.out.println("THE RETURNED VALUE IS: " + value);
    } catch (IOException ioe) {
      System.out.println("Something went wrong: " + ioe);
    }
  }

  private static SecretCache createSecretCache(int cacheSize, long cacheTTL) {
    AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(new BasicAWSCredentials(AWS_KEY, AWS_SECRET));
    AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard()
        .withRegion(REGION)
        .withCredentials(credentials);

    SecretCacheConfiguration cacheConf = new SecretCacheConfiguration().withMaxCacheSize(cacheSize).withCacheItemTTL(
        cacheTTL).withClient(clientBuilder.build());

    return new SecretCache(cacheConf);
  }
}