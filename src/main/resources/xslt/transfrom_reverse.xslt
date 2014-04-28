<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:m0="http://services.samples/xsd"
                version="2.0">
    <xsl:output method="xml" omit-xml-declaration="yes" indent="no"/>
    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="m0:buyStocks">
        <m0:skcotSyub>
            <xsl:for-each select="order">
                <redro xmlns="">
                    <lobmys>
                        <xsl:value-of select="symbol"/>
                    </lobmys>
                    <DIreyub>
                        <xsl:value-of select="buyerID"/>
                    </DIreyub>
                    <ecirp>
                        <xsl:value-of select="price"/>
                    </ecirp>
                    <emulov>
                        <xsl:value-of select="volume"/>
                    </emulov>
                </redro>
            </xsl:for-each>
        </m0:skcotSyub>
    </xsl:template>
</xsl:stylesheet>