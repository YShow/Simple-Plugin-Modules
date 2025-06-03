/**
 * 
 */
/**
 * 
 */
module com.plugin.mine {
    requires mychat;
    requires transitive org.slf4j;

    provides com.yshow.mychat.plugin.Plugin with testeeee.HelloWorldPlugin;
}