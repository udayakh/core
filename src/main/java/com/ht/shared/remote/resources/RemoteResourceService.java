package com.ht.shared.remote.resources;

import java.io.File;

/**
 * Interface for retrieving remotely stored resources.
 * 
 * @author Denny Ayard denny.ayard@revature.com
 */
public interface RemoteResourceService {

  /**
   * Returns true if the resource can be retrieved from the service.
   * 
   * @param fileKey The unique identifier for the target resource.
   * @return true if the resource is present; false otherwise.
   */
  boolean exists(String fileKey);

  /**
   * Returns true if the resource can be retrieved from the service.
   * 
   * @param bucketName The container for the resource.
   * @param fileKey The unique identifier for the target resource.
   * @return true if the resource is present; false otherwise.
   */
  boolean exists(String bucketName, String fileKey);

  /**
   * Returns the URL for a static resource.Currently, the only resources that are allowed to be
   * obtained statically are image files (.png, .gif, .jpg, .jpeg).
   * 
   * @param fileKey The unique identifier for the target resource.
   * @return A Url to the resource.
   */
  String getStaticResourceUrl(String fileKey);

  /**
   * Returns the URL for a dynamic resource. Depending on implementation, this URL may only allow
   * temporary access to the resource.
   * 
   * @param fileKey The unique identifier for the target resource.
   * @return A Url to the resource.
   */
  String getDynamicResourceUrl(String fileKey);

  /**
   * Returns a temporary URL for a dynamic resource (if the service supports such a feature).
   * 
   * @param fileKey The unique identifier for the target resource.
   * @param secondsToLive A positive integer indicating for how long the URL should allow access.
   * @return A Url to the resource.
   * @throws IllegalArgumentException if secondsToLive is not positive.
   */
  String getDynamicResourceUrl(String fileKey, int secondsToLive);

  /**
   * Saves a data sequence (byte[]) to the resource service.
   * 
   * @param fileKey The identifier to use for the resource.
   * @param data A byte array; (most likely a file's contents)
   * @return true if the put request is accepted; false otherwise.
   */
  boolean saveResource(String fileKey, byte[] data);

  /**
   * Uses a File object to save a resource.
   * 
   * @param fileKey The unique identifier for the target resource.
   * @param data A File object representing the resource to save to the remote service.
   * @return true if the put request is accepted; false otherwise.
   */
  boolean saveResource(String fileKey, File data);

  /**
   * Attempts to remove the resource indicated by the fileKey from the remote service.
   * 
   * @param fileKey The unique identifier for the target resource.
   * @return true if the delete request was accepted; false otherwise.
   */
  boolean deleteResource(String fileKey);

  /**
   * Attempts to backup an already saved resource.
   * 
   * @param sourceFileKey The current identifier for the target resource.
   * @param destFileKey The identifier to use for the produced copy.
   * @return true if the copy request is accepted; false otherwise.
   */
  boolean backupResource(String sourceFileKey, String destFileKey);

  /**
   * Attempts to change a resource's file key.
   * 
   * @param sourceFileKey The current identifier for the target resource.
   * @param destFileKey The desired identifier for the the resource.
   * @return true if the move request is accepted; false otherwise.
   */
  boolean moveResource(String sourceFileKey, String destFileKey);

  /**
   * Retrieves the resource indicated by the fileKey and returns it as a byte array.
   * 
   * @param fileKey The unique identifier for the target resource.
   * @return A byte array representing the resource.
   * @throws ResourceNotFoundException Runtime exception indicating failure to generate the array.
   */
  byte[] getResourceAsByteArray(String fileKey);

  /**
   * Generates a url that will cause a download prompt when followed.
   * 
   * @param fileKey The unique identifier of a resource.
   * @param fileName The fileName to use for the expression ("attachment;filename=" + fileName)
   * @return A url such that the response content-disposition will be equal to
   *         ("attachment;filename=" + fileName) or null
   */
  String getDownloadUrl(String fileKey, String fileName);
}
