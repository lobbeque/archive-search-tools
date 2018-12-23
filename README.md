# Archive-search-tools

A set of customized Solr search handlers and components for a [Web archives search engine](https://github.com/lobbeque/archive-search).

   1. ArchiveSearchHandler: a search handler for grouping results
   2. ContextMapComponent: a search component for returning all the hypertext links of a given archived Web page 
   3. SiteLinkComponent: a search component for returning all the hypertext links of a whole archived Web site
   4. TimePickerComponent:  a search component for returning the closest archived Web page to a given date 

## Current dependencies 

   1. [Rivelaine](https://github.com/lobbeque/rivelaine/tree/master/scala)

## Usage 

Download or clone the source file:

```
git clone git@github.com:lobbeque/archive-search-tools.git
```

Build the source code using [sbt](https://www.scala-sbt.org/) and see `~/archive-search-tools/build.sbt` for some specific configurations: 

```
cd ~/archive-search-tools/
sbt assembly
```

Copy the resulting `.jar` file as a library for Solr: 

```
cp ./target/scala-2.10/archive-search-tools-assembly-1.0.0.jar ~/search/solr-5.4.1/server/solr/lib/
```

Restart Solr:

```
cd ~/search/solr-5.4.1/
./restart
```

## Licence

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.