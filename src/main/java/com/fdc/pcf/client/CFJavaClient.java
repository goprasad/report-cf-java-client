package com.fdc.pcf.client;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.cloudfoundry.client.v2.organizations.GetOrganizationUserRolesRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationUserRolesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.UserOrganizationRoleResource;
import org.cloudfoundry.client.v2.spaces.ListSpaceUserRolesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceUserRolesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.spaces.UserSpaceRoleResource;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.AbstractUaaTokenProvider;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;

public class CFJavaClient {

	// private static final String API_HOST = "api.run.pivotal.io";
	// private static final String ORG_NAME = "Gorle";
	// private static final String SPACE_NAME = "development";
	// private static final boolean SKIP_SSL = true;
	// private static final String USER = "gorleteja7@gmail.com";
	// private static final String PASSWORD = "P@ssw0rd";

	private static final String API_HOST = "api.system.us-oma1-inp1.1dc.com";
	private static final String ORG_NAME = "Gorle";
	private static final String SPACE_NAME = "CAT";
	private static final boolean SKIP_SSL = true;
	private static final String USER = "admin";
	private static final String PASSWORD = "f0bf13eaff9c7b222593";

	ConnectionContext connection = DefaultConnectionContext.builder().apiHost(API_HOST).skipSslValidation(SKIP_SSL)
			.build();

	AbstractUaaTokenProvider tokenProvider = createTokenProvider();

	ReactorCloudFoundryClient client = ReactorCloudFoundryClient.builder().connectionContext(connection)
			.tokenProvider(tokenProvider).build();

	protected AbstractUaaTokenProvider createTokenProvider() {

		return PasswordGrantTokenProvider.builder().username(USER).password(PASSWORD).build();

	}

	public static void main(String[] args) throws Exception {
		System.out.println("Starting...");

		PrintWriter pw = new PrintWriter(new File(
				"/Users/f4zmlx9/cf_java_workspace/tmp3/cf-java-client-sample-master/src/main/resources/test_new.csv"));
		StringBuilder sb = new StringBuilder();
		sb.append("Organization");
		sb.append(',');
		sb.append("Space");
		sb.append(',');
		sb.append("User ID");
		sb.append(",");
		sb.append("Assigned Role");
		sb.append('\n');

		ListOrganizationsResponse respall = new CFJavaClient().client.organizations()
				.list(ListOrganizationsRequest.builder().build()).block();
		List<OrganizationResource> listorgs = respall.getResources();

		for (OrganizationResource eachOrg : listorgs) {

			GetOrganizationUserRolesResponse orgUserRolesResponse = new CFJavaClient().client.organizations()
					.getUserRoles(GetOrganizationUserRolesRequest.builder()
							.organizationId(eachOrg.getMetadata().getId()).build())
					.block();

			List<UserOrganizationRoleResource> listUserOrganizationRoleResource = orgUserRolesResponse.getResources();

			for (UserOrganizationRoleResource eachOrgUser : listUserOrganizationRoleResource) {

				System.out.println("Organization Name: " + eachOrg.getEntity().getName() + "  UserName  :"
						+ eachOrgUser.getEntity().getUsername() + "  Roles : "
						+ eachOrgUser.getEntity().getOrganizationRoles());

				if (eachOrgUser.getEntity().getUsername() != null
						&& !(eachOrgUser.getEntity().getUsername().trim().isEmpty())) {
					sb.append(eachOrg.getEntity().getName());
					sb.append(',');
					sb.append("");
					sb.append(',');
					sb.append(eachOrgUser.getEntity().getUsername());
					sb.append(",");
					sb.append(createSpacedStringsFromList(eachOrgUser.getEntity().getOrganizationRoles()));
					sb.append('\n');
				}

			}

			ListSpacesResponse spaceResp = new CFJavaClient().client.spaces()
					.list(ListSpacesRequest.builder().organizationId(eachOrg.getMetadata().getId()).build()).block();

			List<SpaceResource> listSpaces = spaceResp.getResources();

			for (SpaceResource eachSpace : listSpaces) {

				ListSpaceUserRolesResponse listSpaceUserRolesResponse = new CFJavaClient().client.spaces()
						.listUserRoles(
								ListSpaceUserRolesRequest.builder().spaceId(eachSpace.getMetadata().getId()).build())
						.block();
				List<UserSpaceRoleResource> ListUserSpaceRoleResource = listSpaceUserRolesResponse.getResources();
				System.out.println("all users for given Space:" + eachSpace.getEntity().getName());
				for (UserSpaceRoleResource eachSpaceUser : ListUserSpaceRoleResource) {

					System.out.println("SpaceName: " + eachSpace.getEntity().getName() + "  UserName  :"
							+ eachSpaceUser.getEntity().getUsername() + "  Roles : "
							+ eachSpaceUser.getEntity().getSpaceRoles());

					if (eachSpaceUser.getEntity().getUsername() != null
							&& !(eachSpaceUser.getEntity().getUsername().trim().isEmpty())) {
						sb.append(eachOrg.getEntity().getName());
						sb.append(',');
						sb.append(eachSpace.getEntity().getName());
						sb.append(',');
						sb.append(eachSpaceUser.getEntity().getUsername());
						sb.append(",");
						sb.append(createSpacedStringsFromList(eachSpaceUser.getEntity().getSpaceRoles()));
						sb.append('\n');

					}

				}
			}
		}

		pw.write(sb.toString());
		pw.close();
		System.out.println("done!!!!!!!!!!!!!!!");
	}

	private static String createSpacedStringsFromList(List<String> list) {
		StringBuffer sb = new StringBuffer();

		for (String role : list) {
			sb.append(role);
			sb.append(" ");
		}

		return sb.toString();

	}

}
