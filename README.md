"# pc_keyboard：PC用キーボードを用いた演奏システム"

こちらはPC用キーボードを用いて演奏を行うシステムになります。

演奏する音の強さを機械学習で自動的に予測します。

これによって、PC用キーボードを用いてリアルタイムに強弱を変化させることを期待します。

Mavenを使用して作成しました。

Mavenのインストール方法については以下の通りです。

＜インストール方法＞

現在編集中



pom.xmlにあるgroupIdはMavenプロジェクトの作成時に指定したgroupIdと同じになります。

artifactIdはMavenプロジェクトの作成時に指定したプロジェクト名になります。

pom.xmlのexec-maven-plugin内に、configurationがあります。

この中のadditionalClasspathelemntにはcmx内の各ファイルへの絶対パスを記述します。cmxフォルダをダウンロードしたら、cmx内にあるjarファイルへのパスを各自変更してください。

演奏システムを実行するには、src/main/java にある、”ModelServer.java”, ”pc_keyboard3.java”を同じディレクトリに置き、”pc_keyboard3.java”を実行します。

予測に使用する学習済みモデルはサンプルを構成する要素がmymodelにありますので、ローカルファイルを作成し、これらの要素を作成したファイルに置くようにしてください。



