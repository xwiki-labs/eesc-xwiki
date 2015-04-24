##EESC XWiki Service##
###Configuration de l'API vers l'ENT###
Différentes implémentations sont possibles pour l'accès à l'API proposée par l'ENT et concernant les utilisateurs et groupes et leurs droits d'accès.  Actuellement, seule l'implémentation test existe.  Pour configurer une implémentation particulière, il faut modifier le fichier \<INSTALL>/xwiki/WEB-INF/xwiki.properties en ajoutant la ligne suivante.

```
eesc.service=test
```

####Spécificités de l'implémentation default####
Si aucun configuration n'est effectuée dans le fichier \<INSTALL>/xwiki/WEB-INF/xwiki.properties concernant la variable eesc.service, alors c'est l'implémentation par défaut qui sera utilisée.  L'implémentation par défaut se connecte au service d'annuaire proposé par SOPRA qui se trouve actuellement à l'URL suivante https://cg93.monent.fr/interop/annuaire/.  Le service propose actuellement une liste d'utilisateurs de démonstration cohérent avec le service de CAS https://demo.monent.fr:443/connexion/ (voir ci-dessous).

La liste des utilisateurs proposée est la suivante.

| Prénom  | Nom | Login  | Rôle |
| ------------- | ------------- | ------------- | ------------- |
| Marie  | BIHOUE  | marie.bihoue   | étudiant |
| Rita  | SANCHEZ  | rita.sanchez  | enseignant |
| Juliette  | BIHOUE  | juliette.bihoue  | parent |
| Richard  | NACCI  | richard.nacci  | administrateur local |
| Etienne  | DUJARDIN  | etienne.dujardin  | staff |
| Arnaud  | REGNAL  | arnaud.regnal  | invité |

####Spécificités de l'implémentation test####
L'implémentation test possède certaines spécificités que n'auront pas les autres implémentations.  En effet, cette implémentation simule un fonctionnement normal en proposant une fausse base de données contenant les informations nécessaires.  Il faut cependant peupler cette base de données qui est un simple fichier texte avec un format particulier.
Le fichier texte est \<INSTALL>/xwiki/WEB-INF/eesc.properties et contient deux types d'informations : les utilisateurs et les groupes.
Les utilisateurs

Les informations pour un utilisateur sont les suivantes :

```
user.<uid>=<name>
user.<uid>.status=<status>
user.<uid>.etabid=<etabid-1>,<etabid-2>,<etabid-3>
```
où les différentes informations entre < et > doivent être remplies selon les conditions suivantes :

* \<uid> est l'identifiant alphanumérique de l'utilisateur; il doit être unique dans le fichier.
* \<name> est le nom de l'utilisateur qui sera affiché dans l'interface utilisateur.
* \<status> est le statut de l'utilisateur qui sera une des valeurs suivantes : TEACHER, STUDENT, LOCAL_ADMIN, STAFF, PARENT ou GUEST.
* \<etabid-#> est l'identifiant de l'établissement dont l'utilisateur fait parti; l'utilisateur peut appartenir à plusieurs établissements qui seront séparés par une virgule et sans espace

Les groupes

Les informations pour les groupes sont les suivantes :
```
group.<gid>=Un
group.<gid>.type=<type>
group.<gid>.user.<uid-1>=
group.<gid>.user.<uid-2>=
group.<gid>.user.<uid-3>=
```
où les différentes informations entre < et > doivent être remplies selon les conditions suivantes :

* \<gid> est l'identifiant alphanumérique du groupe; il doit être unique dans le fichier.
* \<type> est le type du groupe qui sera une des valeurs suivantes : PRIVATE, RESTRICTED ou PUBLIC.
* \<uid-#> est l'identifiant alphanumérique unique d'un des utilisateurs de ce groupe; plusieurs lignes de ce type peuvent être ajoutées pour ajouter les différents utilisateurs; l'existence de la propriété suffit pour ajouter l'utilisateur, la propriété n'attend pas de valeur.

Exemple de fichier

Le fichier doit utiliser le format d'encodage iso8859-1 (aussi connu sous le nom de latin1) si vous souhaitez utiliser des caractères hors de l'encodage ASCII.

On considère les personnes suivantes :

| Prénom  | Nom | Login  | Rôle |
| ------------- | ------------- | ------------- | ------------- |
| Fanny  | PARENT  | fanny.parent | étudiant |
| Rita  | SANCHEZ  | rita.sanchez  | enseignant |
| Pierre | PARENT  | pierre.parent  | parent |
| Richard  | NACCI  | richard.nacci  | administrateur local |
| Etienne  | DUJARDIN  | etienne.dujardin  | staff |
| Arnaud  | REGNAL  | arnaud.regnal  | invité |

et les groupes suivants :

| Groupe  | Membres |
| ------------- | -------------|
| Administration | Richard NACCI |
| Classe de 6e A | Fanny PARENT, Rita SANCHEZ, Pierre PARENT |
| College | Tous les membres |

et enfin les établissements suivants :

| Établissement | Membres |
| ------------- | ------- |
| Lycée Voltaire | Tous les membres |
| Collège Victor Hugo | Richard NACCI et Arnaud RÉGNAL |

```
user.320000000000046020=Fanny PARENT
user.320000000000046020.status=STUDENT
user.320000000000046020.etabid=voltaire
user.310750000000010020=Rita SANCHEZ
user.310750000000010020.status=TEACHER
user.310750000000010020.etabid=voltaire
user.330000000000126020=Pierre PARENT
user.330000000000126020.status=PARENT
user.330000000000126020.etabid=voltaire
user.310750000000013020=Richard NACCI
user.310750000000013020.status=LOCAL_ADMIN
user.310750000000013020.etabid=voltaire,victor-hugo
user.310750000000012020=Étienne DUJARDIN
user.310750000000012020.status=STAFF
user.310750000000012020.etabid=voltaire
user.5000=Arnaud RÉGNAL
user.5000.status=GUEST
user.5000.etabid=voltaire,victor-hugo

group.0001=Administration
group.0001.type=PRIVATE
group.0001.user.310750000000013020=

group.0002=Classe de 6è A
group.0002.type=RESTRICTED
group.0002.user.320000000000046020=
group.0002.user.310750000000010020=
group.0002.user.330000000000126020=

group.0003=Collège
group.0003.type=PUBLIC
group.0003.user.320000000000046020=
group.0003.user.310750000000010020=
group.0003.user.330000000000126020=
group.0003.user.310750000000013020=
group.0003.user.310750000000012020=
group.0003.user.5000=
```
Dans le cas où XWiki utilise le service d'authentification externe (voir ci-dessous), l'UID des utilisateurs doit correspondre à l'identifiant utilisé au niveau de l'ENT (ici, l'utilisateur Fanny PARENT possède l'identifiant 320000000000046020).
