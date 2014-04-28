<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:m="http://services.samples/xsd"
                version="2.0">
    <xsl:output method="xml" omit-xml-declaration="yes" indent="no"/>
    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="m:skcotSyub">
        <m:buyStocks>
            <xsl:for-each select="redro">
                <order xmlns="">
                    <symbol>
                        <xsl:value-of select="lobmys"/>
                    </symbol>
                    <buyerID>
                        <xsl:value-of select="DIreyub"/>
                    </buyerID>
                    <price>
                        <xsl:value-of select="ecirp"/>
                    </price>
                    <volume>
                        <xsl:value-of select="emulov"/>
                    </volume>
                </order>
            </xsl:for-each>
        </m:buyStocks>
    </xsl:template>
</xsl:stylesheet>