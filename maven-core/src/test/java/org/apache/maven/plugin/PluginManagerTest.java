package org.apache.maven.plugin;

import org.apache.maven.AbstractCoreMavenComponentTest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.codehaus.plexus.component.annotations.Requirement;

public class PluginManagerTest
    extends AbstractCoreMavenComponentTest
{
    @Requirement
    private PluginManager pluginManager;
    
    protected void setUp()
        throws Exception
    {
        super.setUp();
        pluginManager = lookup( PluginManager.class );
    }

    protected String getProjectsDirectory()
    {
        return "src/test/projects/lifecycle-executor";
    }
                
    // -----------------------------------------------------------------------------------------------
    // Tests which exercise the lifecycle executor when it is dealing with individual goals.
    // -----------------------------------------------------------------------------------------------
    
    //TODO: These two tests display a lack of symmetry with respect to the input which is a free form string and the
    //      mojo descriptor which comes back. All the free form parsing needs to be done somewhere else, this is
    //      really the function of the CLI, and then the pre-processing of that output still needs to be fed into
    //      a hinting process which helps flesh out the full specification of the plugin. The plugin manager should
    //      only deal in concrete terms -- all version finding mumbo jumbo is a customization to base functionality
    //      the plugin manager provides.
    
    public void testRemoteResourcesPlugin()
        throws Exception
    {
        MavenSession session = createMavenSession( getProject( "project-with-inheritance" ) );       
        String goal = "process";
        
        Plugin plugin = new Plugin();
        plugin.setGroupId( "org.apache.maven.plugins" );
        plugin.setArtifactId( "maven-remote-resources-plugin" );
        plugin.setVersion( "1.0" );
        
        MojoDescriptor mojoDescriptor = pluginManager.getMojoDescriptor( plugin, goal, session );
        assertPluginDescriptor( mojoDescriptor, "org.apache.maven.plugins", "maven-remote-resources-plugin", "1.0" );
        MojoExecution mojoExecution = new MojoExecution( mojoDescriptor );
        pluginManager.executeMojo( session.getCurrentProject(), mojoExecution, session );
    }

    public void testSurefirePlugin()
        throws Exception
    {
        MavenSession session = createMavenSession( getProject( "project-with-inheritance" ) );       
        String goal = "test";

        Plugin plugin = new Plugin();
        plugin.setGroupId( "org.apache.maven.plugins" );
        plugin.setArtifactId( "maven-surefire-plugin" );
        plugin.setVersion( "2.4.2" );
        
        MojoDescriptor mojoDescriptor = pluginManager.getMojoDescriptor( plugin, goal, session );                
        assertPluginDescriptor( mojoDescriptor, "org.apache.maven.plugins", "maven-surefire-plugin", "2.4.2" );
        MojoExecution mojoExecution = new MojoExecution( mojoDescriptor );
        pluginManager.executeMojo( session.getCurrentProject(), mojoExecution, session );
    }
    
    // -----------------------------------------------------------------------------------------------
    // Testing help
    // -----------------------------------------------------------------------------------------------

    protected void assertPluginDescriptor( MojoDescriptor mojoDescriptor, String groupId, String artifactId, String version )
    {
        assertNotNull( mojoDescriptor );        
        PluginDescriptor pd = mojoDescriptor.getPluginDescriptor();
        assertNotNull( pd );
        assertEquals( groupId, pd.getGroupId() );
        assertEquals( artifactId, pd.getArtifactId() );
        assertEquals( version, pd.getVersion() );        
    }       
}
