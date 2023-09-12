TradeCraftというサーバーで使われているプラグインです  

## サーバーへの導入の手順(step-by-step)
・ サーバーのpluginsフォルダ内にこのプラグインを入れて下さい  
・ サーバーのpluginsフォルダ内に[plugin.yml]( https://github.com/RIB-Lab/TradeCore/blob/master/src/main/resources/plugin.yml ) に記されているdepend(前提)プラグインを全て導入してください  
・ このリポジトリの[plugins]( https://github.com/RIB-Lab/TradeCore/tree/master/run/plugins ) 内のコンフィグを全てサーバーにコピーしてください (アイテムデータなどが含まれているので、ないと動きません)  
・ spigot.ymlの log-named-deaths: をfalseにしてください  
・ servers.propertiesの require-resource-pack= をtrueにしてください  
    resource-pack= と resource-pack-sha1= を [TradeCore_Resources]( https://github.com/RIB-Lab/TradeCore_Resources )に設定してください (設定しないとGuiがnullになります)  
・ worldフォルダ内に[データパック]( https://github.com/RIB-Lab/TradeCore_Datapack )を導入してください　(入れないと装備のテクスチャがnullになります)  